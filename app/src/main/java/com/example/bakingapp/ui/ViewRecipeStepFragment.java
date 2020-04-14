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
import com.example.bakingapp.data.model.Step;
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

    private RecipeSharedViewModel viewModel;
    private SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = true;
    private long playBackPosition;
    private int currentWindow;
    private Bundle savedState;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // update toolbar
        updateToolbar();

        // Initialize Event Listeners
        initListeners();

        // Restore instance state
        savedState = savedInstanceState;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);
        outState.putLong(KEY_PLAY_BACK_POSITION, playBackPosition);
        outState.putInt(KEY_CURRENT_WINDOW, currentWindow);
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
        initPlayer();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    private void updateToolbar() {
        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(viewModel.getSelectedRecipeName());
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initPlayer() {
        if (exoPlayer != null) return;
        exoPlayer = new SimpleExoPlayer.Builder(requireContext())
                .setUseLazyPreparation(true)
                .build();
        exoPlayer.setPlayWhenReady(playWhenReady);
        playerView.setPlayer(exoPlayer);
        initExoPlayerListeners();
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

    private void restorePlayerState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) return;
        playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY, true);
        playBackPosition = savedInstanceState.getLong(KEY_PLAY_BACK_POSITION, 0);
        currentWindow = savedInstanceState.getInt(KEY_CURRENT_WINDOW, 0);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(playBackPosition);
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

        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        resizeVideoPlayer();
    }

    private void initListeners() {
        viewModel.getSelectedStep().observe(getViewLifecycleOwner(), this::showStepData);
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
                showErrorMessage(errMsg);
            }
        });
    }

    private void showErrorMessage(String errMsg) {
        errorPlayerError.setVisibility(View.VISIBLE);
        errorPlayerError.setText(errMsg);
    }

    private void showStepData(Step step) {
        // Prepare Video Url
        String videoUrl = !step.getVideoURL().isEmpty()
                ? step.getVideoURL()
                : !step.getThumbnailURL().isEmpty()
                ? step.getThumbnailURL()
                : "";

        playerView.setVisibility(videoUrl.isEmpty() ? View.GONE : View.VISIBLE);
        stepDescription.setText(step.getDescription());
        buttonNext.setEnabled(viewModel.hasNext());
        buttonPrev.setEnabled(viewModel.hasPrev());

        // Don't load video in case there is no video URL
        if (videoUrl.isEmpty()) return;

        Uri uri = Uri.parse(videoUrl);
        MediaSource videoSource = buildMediaSource(uri);
        exoPlayer.prepare(videoSource);

        if (savedState != null) {
            // restore instance state in the case of a configuration change
            restorePlayerState(savedState);
            savedState = null;
        }
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
