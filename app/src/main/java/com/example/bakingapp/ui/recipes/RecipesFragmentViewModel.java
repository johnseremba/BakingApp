package com.example.bakingapp.ui.recipes;

import androidx.lifecycle.ViewModel;

import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;

import java.util.List;

public class RecipesFragmentViewModel extends ViewModel {
    private Repository repository = Repository.getInstance();
    private List<Recipe> recipes = repository.getRecipes();

    public List<Recipe> getRecipes() {
        return recipes;
    }
}
