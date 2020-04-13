package com.example.bakingapp.ui;


import android.content.res.Configuration;
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
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.util.InjectorUtils;
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
    private static final String KEY_PLAY_WHEN_READY = "KEY_PLAY_WHEN_READY";
    private static final String KEY_PLAY_BACK_POSITION = "KEY_PLAY_BACK_POSITION";
    private static final String KEY_CURRENT_WINDOW = "KEY_CURRENT_WINDOW";
    private static final String KEY_VIDEO_URL = "KEY_VIDEO_URL";

    private RecipeSharedViewModel viewModel;
    private SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = false;
    private long playBackPosition;
    private int currentWindow;
    private String videoUrl = "";

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
                InjectorUtils.provideSharedViewModelFactory()).get(RecipeSharedViewModel.class);
        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY, false);
            playBackPosition = savedInstanceState.getLong(KEY_PLAY_BACK_POSITION, 0);
            currentWindow = savedInstanceState.getInt(KEY_CURRENT_WINDOW, 0);
            videoUrl = savedInstanceState.getString(KEY_VIDEO_URL, "");
        }
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

        initListeners();
        loadRecipeData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);
        outState.putLong(KEY_PLAY_BACK_POSITION, playBackPosition);
        outState.putInt(KEY_CURRENT_WINDOW, currentWindow);
        outState.putString(KEY_VIDEO_URL, videoUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer == null) initPlayer();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer != null) releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exoPlayer != null) releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (exoPlayer != null) releasePlayer();
    }

    private void initPlayer() {
        exoPlayer = new SimpleExoPlayer.Builder(requireContext())
                .setUseLazyPreparation(true)
                .build();
        initExoPlayerListeners();

        playerView.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playBackPosition);
        showRecipeVideo(videoUrl);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playBackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void resizeVideoPlayer() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.height = displayMetrics.heightPixels;
        playerView.setLayoutParams(params);
    }

    private void hideSystemUi() {
        if (getResources().getBoolean(R.bool.isLarge)) return;
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        resizeVideoPlayer();
    }

    private void initListeners() {
        buttonNext.setOnClickListener(v -> viewModel.setSelectedStep(viewModel.getNextStep()));
        buttonPrev.setOnClickListener(v -> viewModel.setSelectedStep(viewModel.getPrevStep()));
    }

    private void initExoPlayerListeners() {
        exoPlayer.addListener(new Player.EventListener() {
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
        viewModel.getSelectedStep().observe(getViewLifecycleOwner(), step -> {
            stepDescription.setText(step.getDescription());
            buttonNext.setEnabled(viewModel.hasNext());
            buttonPrev.setEnabled(viewModel.hasPrev());

            // Prepare Video playback
            String videoUrl = !step.getVideoURL().isEmpty()
                    ? step.getVideoURL()
                    : !step.getThumbnailURL().isEmpty()
                    ? step.getThumbnailURL()
                    : "";
            this.videoUrl = videoUrl;
            showRecipeVideo(videoUrl);
        });
    }

    private void showRecipeVideo(String videoUrl) {
        playerView.setVisibility(videoUrl.isEmpty() ? View.GONE : View.VISIBLE);
        if (videoUrl.isEmpty()) return;
        Uri uri = Uri.parse(videoUrl);
        MediaSource videoSource = buildMediaSource(uri);
        exoPlayer.prepare(videoSource);
        exoPlayer.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(buildDataSourceFactory()).createMediaSource(uri);
    }

    private DataSource.Factory buildDataSourceFactory() {
        return new DefaultDataSourceFactory(requireContext(), buildHttpDataSourceFactory());
    }

    private DefaultHttpDataSourceFactory buildHttpDataSourceFactory() {
        String userAgent = Util.getUserAgent(requireContext(), getString(R.string.app_name));
        return new DefaultHttpDataSourceFactory(userAgent);
    }
}
