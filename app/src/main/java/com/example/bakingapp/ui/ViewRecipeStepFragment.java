package com.example.bakingapp.ui;


import android.content.res.Configuration;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewRecipeStepFragment extends Fragment {
    public static final String TAG = ViewRecipeStepFragment.class.getSimpleName();

    private RecipeSharedViewModel viewModel;
    private SimpleExoPlayer exoPlayer;

    @BindView(R.id.player_view)
    PlayerView playerView;

    @BindView(R.id.text_step_description)
    TextView stepDescription;

    @BindView(R.id.error_player_error)
    TextView errorPlayerError;

    @BindView(R.id.button_prev)
    Button buttonPrev;

    @BindView(R.id.button_next)
    Button buttonNext;

    public ViewRecipeStepFragment() {
        // Required empty public constructor
    }

    public static ViewRecipeStepFragment getInstance() {
        return new ViewRecipeStepFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_recipe_step, container, false);
        ButterKnife.bind(this, view);
        viewModel = new ViewModelProvider(requireActivity(),
                new SharedViewModelFactory(Repository.getInstance())).get(RecipeSharedViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // update toolbar
        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewModel.getSelectedRecipeName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // Instantiate exo player
        initPlayer();
        initListeners();
        loadRecipeData();
    }

    private void initPlayer() {
        exoPlayer = new SimpleExoPlayer.Builder(requireContext()).build();
        playerView.setPlayer(exoPlayer);
        exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Set the player to fullscreen when in landscape mode
            resizeVideoPlayer();

            // Hide appbar
            hideAppBar();
        }
    }

    private void resizeVideoPlayer() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.height = displayMetrics.heightPixels;
        playerView.setLayoutParams(params);
        exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
    }

    private void hideAppBar() {
        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    private void releasePlayer() {
        exoPlayer.stop();
        exoPlayer.release();
        exoPlayer = null;
    }

    private DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(requireContext(), buildHttpDataSourceFactory());
    }

    private DefaultHttpDataSourceFactory buildHttpDataSourceFactory() {
        String userAgent = Util.getUserAgent(requireContext(), getString(R.string.app_name));
        return new DefaultHttpDataSourceFactory(userAgent);
    }

    private void initListeners() {
        buttonNext.setOnClickListener(v -> viewModel.setSelectedStep(viewModel.getNextStep()));
        buttonPrev.setOnClickListener(v -> viewModel.setSelectedStep(viewModel.getPrevStep()));

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == PlaybackState.STATE_PLAYING) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        hideAppBar();
                    }
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String errMsg = error.getLocalizedMessage();

                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    IOException cause = error.getSourceException();
                    if (cause instanceof HttpDataSource.HttpDataSourceException) {
                        HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                        errMsg = httpError.getLocalizedMessage();
                        if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                            HttpDataSource.InvalidResponseCodeException responseError = (HttpDataSource.InvalidResponseCodeException) httpError;
                            errMsg = responseError.getLocalizedMessage();
                        }
                    }
                }

                errorPlayerError.setVisibility(View.VISIBLE);
                errorPlayerError.setText(errMsg);
            }
        });
    }

    private void loadRecipeData() {
        viewModel.getSelectedStep().observe(this, step -> {
            stepDescription.setText(step.getDescription());
            buttonNext.setEnabled(viewModel.hasNext());
            buttonPrev.setEnabled(viewModel.hasPrev());

            // Prepare Video playback
            String videoUrl = !step.getVideoURL().isEmpty()
                    ? step.getVideoURL()
                    : !step.getThumbnailURL().isEmpty()
                    ? step.getThumbnailURL()
                    : "";
            showRecipeVideo(videoUrl);
        });
    }

    private void showRecipeVideo(String videoUrl) {
        playerView.setVisibility(videoUrl.isEmpty() ? View.GONE : View.VISIBLE);
        if (videoUrl.isEmpty()) return;
        Uri uri = Uri.parse(videoUrl);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri);
        exoPlayer.prepare(videoSource);
        exoPlayer.setPlayWhenReady(true);
    }
}
