package com.example.bakingapp;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.ui.RecipeStepFragment;
import com.example.bakingapp.ui.RecipesFragment;
import com.example.bakingapp.ui.ViewRecipeStepFragment;
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;
import com.example.bakingapp.widget.BakingAppWidgetProvider;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private RecipeSharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this,
                new SharedViewModelFactory(Repository.getInstance())).get(RecipeSharedViewModel.class);

        fragmentManager = getSupportFragmentManager();

        if (getIntent() != null) {
            // If the app is launched from the widget, load the provided recipe and display it
            Recipe recipe = getIntent().getParcelableExtra(BakingAppWidgetProvider.EXTRA_WIDGET_RECIPE);
            if (recipe != null) {
                viewModel.setSelectedRecipe(recipe);
                RecipeStepFragment recipeStepFragment = RecipeStepFragment.getInstance();
                addFragment(recipeStepFragment, RecipeStepFragment.TAG);
                return;
            }
        }

        if (savedInstanceState == null) {
            RecipesFragment recipesFragment = RecipesFragment.newInstance();
            addFragment(recipesFragment, RecipesFragment.TAG);
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

    @Override
    public void onBackPressed() {
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount == 1) {
            finish();
        } else {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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
