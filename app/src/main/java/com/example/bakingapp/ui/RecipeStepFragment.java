package com.example.bakingapp.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.data.model.Step;
import com.example.bakingapp.ui.adapters.IngredientsAdapter;
import com.example.bakingapp.ui.adapters.RecipeStepsAdapter;
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepFragment extends Fragment {
    public static final String TAG = RecipeStepFragment.class.getSimpleName();
    private RecipeSharedViewModel viewModel;
    private IngredientsAdapter ingredientsAdapter;
    private RecipeStepsAdapter recipeStepsAdapter;
    private RecipeStepFragmentInteractionListener mListener;

    @BindView(R.id.text_recipe_label)
    TextView textRecipeLabel;

    @BindView(R.id.image_recipe_image)
    ImageView recipeImage;

    @BindView(R.id.ingredients_recycler_view)
    RecyclerView ingredientsRecyclerView;

    @BindView(R.id.recipe_steps_recycler_view)
    RecyclerView recipeStepsRecyclerView;

    public RecipeStepFragment() {
        // Required empty public constructor
    }

    public static RecipeStepFragment getInstance() {
        return new RecipeStepFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
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

        initRecyclerViews();
        loadRecipe();
    }

    private void initRecyclerViews() {
        ingredientsAdapter = new IngredientsAdapter();
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        recipeStepsAdapter = new RecipeStepsAdapter(this::onClickStep);
        recipeStepsRecyclerView.setAdapter(recipeStepsAdapter);
    }

    private void onClickStep(Step step) {
        viewModel.setSelectedStep(step);
        mListener.showViewRecipeStepFragment();
    }

    private void loadRecipe() {
        viewModel.getSelectedRecipe().observe(this, this::displayRecipeData);
    }

    private void displayRecipeData(Recipe recipe) {
        textRecipeLabel.setText(getString(R.string.title_recipe, recipe.getName()));

        // Load recipe image if it exists
        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            Picasso.get().load(recipe.getImage()).into(recipeImage);
        }

        // Load recipes
        ingredientsAdapter.setIngredients(recipe.getIngredients());
        recipeStepsAdapter.setSteps(recipe.getSteps());
    }

    public void setListener(RecipeStepFragmentInteractionListener listener) {
        mListener = listener;
    }

    public interface RecipeStepFragmentInteractionListener {
        void showViewRecipeStepFragment();
    }
}
