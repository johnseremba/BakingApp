package com.example.bakingapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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
    private boolean mTwoPane = false;
    private FrameLayout viewRecipeStepContainer;
    private View viewDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // Check if activity is launched with a two pane layout
        viewRecipeStepContainer = (FrameLayout) findViewById(R.id.fragment_detail_container);
        viewDivider = (View) findViewById(R.id.view_divider);
        mTwoPane = (viewRecipeStepContainer != null);

        if (getIntent() != null) {
            // If the app is launched from the widget, load the provided recipe and display it
            Recipe recipe = getIntent().getParcelableExtra(BakingAppWidgetProvider.EXTRA_WIDGET_RECIPE);
            if (recipe != null) {
                RecipeSharedViewModel viewModel = new ViewModelProvider(this,
                        new SharedViewModelFactory(Repository.getInstance())).get(RecipeSharedViewModel.class);
                viewModel.setSelectedRecipe(recipe);
                RecipeStepFragment recipeStepFragment = RecipeStepFragment.getInstance();
                addFragment(recipeStepFragment, RecipeStepFragment.TAG);
                return;
            }
        }

        // Handle configuration changes. Don't instantiate a new fragment.
        if (savedInstanceState == null) {
            RecipesFragment recipesFragment = RecipesFragment.newInstance();
            addFragment(recipesFragment, RecipesFragment.TAG);
        }
    }

    private void addFragment(Fragment fragment, String tag) {
        if (mTwoPane && fragment instanceof ViewRecipeStepFragment) {
            toggleTwoPaneLayout(true);
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment, tag)
                    .commit();
            return;
        }
        toggleTwoPaneLayout(false);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private void toggleTwoPaneLayout(boolean showSecondPane) {
        if (mTwoPane) {
            viewRecipeStepContainer.setVisibility(showSecondPane ? View.VISIBLE : View.GONE);
            viewDivider.setVisibility(showSecondPane ? View.VISIBLE : View.GONE);
        }
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
            if (mTwoPane) toggleTwoPaneLayout(false);
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
