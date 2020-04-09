package com.example.bakingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.bakingapp.ui.RecipeStepFragment;
import com.example.bakingapp.ui.RecipesFragment;
import com.example.bakingapp.ui.ViewRecipeStepFragment;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        RecipesFragment recipesFragment = RecipesFragment.newInstance();
        addFragment(recipesFragment, RecipesFragment.TAG);
    }

    public void updateAppToolbar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void addFragment(Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        // Handle Fragment interaction
        if (fragment instanceof RecipesFragment) {
            ((RecipesFragment) fragment).setListener(recipesFragListener);
        } else if (fragment instanceof RecipeStepFragment) {
            ((RecipeStepFragment) fragment).setListener(stepFragListener);
        }
    }

    private RecipesFragment.RecipesFragmentInteractionListener recipesFragListener = () -> {
        RecipeStepFragment recipeStepFragment = RecipeStepFragment.getInstance();
        addFragment(recipeStepFragment, RecipeStepFragment.TAG);
    };

    private RecipeStepFragment.RecipeStepFragmentInteractionListener stepFragListener = () -> {
        ViewRecipeStepFragment viewRecipeStepFragment = ViewRecipeStepFragment.getInstance();
        addFragment(viewRecipeStepFragment, ViewRecipeStepFragment.TAG);
    };
}
