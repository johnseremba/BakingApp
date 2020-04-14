package com.example.bakingapp.ui;

import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.ui.adapters.RecipesAdapter;
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.util.InjectorUtils;
import com.example.bakingapp.widget.BakingAppWidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RecipesFragment extends Fragment {
    public static final String TAG = RecipesFragment.class.getSimpleName();
    private Unbinder unbinder;
    private RecipeSharedViewModel viewModel;
    private RecipesFragmentInteractionListener mListener;
    private RecipesAdapter recipesAdapter;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.text_error_msg)
    TextView errMsg;

    public RecipesFragment() {
        // Required empty public constructor
    }

    public static RecipesFragment newInstance() {
        return new RecipesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewModel = new ViewModelProvider(requireActivity(),
                InjectorUtils.provideSharedViewModelFactory()).get(RecipeSharedViewModel.class);
        // Show progress bar to fetch recipes for the first time
        if (savedInstanceState == null) {
            showLoading(true);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setListener(RecipesFragmentInteractionListener listener) {
        mListener = listener;
    }

    private void initUI() {
        // update toolbar
        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_baking_time));
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        // Populate RecyclerView
        if (getResources().getBoolean(R.bool.isLarge)) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
            recipesRecyclerView.setLayoutManager(layoutManager);
        }
        recipesAdapter = new RecipesAdapter(this::onClickRecipe);
        recipesRecyclerView.setAdapter(recipesAdapter);
    }

    private void initListeners() {
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            recipesAdapter.setRecipes(recipes);
            showLoading(false);
        });

        viewModel.getErrors().observe(getViewLifecycleOwner(), error -> {
            errMsg.setText(error);
            showLoading(false);
        });
    }

    private void showLoading(boolean status) {
        progressBar.setVisibility(status ? View.VISIBLE : View.GONE);
    }

    private void onClickRecipe(Recipe recipe) {
        viewModel.setSelectedRecipe(recipe);
        mListener.showRecipeStepsFragment();

        // update App widget with the selected recipe
        BakingAppWidgetProvider.sendRefreshBroadcast(requireContext().getApplicationContext(), recipe);
    }

    public interface RecipesFragmentInteractionListener {
        void showRecipeStepsFragment();
    }
}
