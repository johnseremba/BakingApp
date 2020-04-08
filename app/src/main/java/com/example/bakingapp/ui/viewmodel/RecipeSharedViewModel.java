package com.example.bakingapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.data.model.Step;

import java.util.List;

public class RecipeSharedViewModel extends ViewModel {
    private Repository repository;
    private List<Recipe> recipes;
    private MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
    private MutableLiveData<Step> selectedStep = new MutableLiveData<>();

    public RecipeSharedViewModel(Repository repository) {
        this.repository = repository;
    }

    public List<Recipe> getRecipes() {
        if (recipes == null) {
            recipes = repository.getRecipes();
        }
        return recipes;
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(Recipe recipe) {
        selectedRecipe.setValue(recipe);
    }

    public LiveData<Step> getSelectedStep() {
        return selectedStep;
    }

    public void setSelectedStep(Step step) {
        selectedStep.setValue(step);
    }
}
