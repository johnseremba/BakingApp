package com.example.bakingapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.data.model.Step;
import com.example.bakingapp.util.InjectorUtils;

import java.util.List;

public class RecipeSharedViewModel extends ViewModel {
    private Repository repository;
    private String selectedRecipeName;

    private LiveData<List<Recipe>> recipes;
    private LiveData<String> errorMsg;
    private MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
    private MutableLiveData<Step> selectedStep = new MutableLiveData<>();

    public RecipeSharedViewModel(Repository repository) {
        this.repository = repository;
        recipes = repository.getRecipes(InjectorUtils.provideIdlingResource());
        errorMsg = repository.getErrors();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<String> getErrors() {
        return errorMsg;
    }

    public LiveData<Recipe> getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(Recipe recipe) {
        selectedRecipeName = recipe.getName();
        selectedRecipe.setValue(recipe);

        // Update repo with selected recipe
        repository.setWidgetRecipe(recipe);
    }

    public LiveData<Step> getSelectedStep() {
        return selectedStep;
    }

    public void setSelectedStep(Step step) {
        selectedStep.setValue(step);
    }

    public String getSelectedRecipeName() {
        return selectedRecipeName;
    }

    public boolean hasNext() {
        return getNextStep() != null;
    }

    public boolean hasPrev() {
        return getPrevStep() != null;
    }

    public Step getNextStep() {
        if (selectedStep.getValue() == null || selectedRecipe.getValue() == null) return null;
        int currentStepId = selectedStep.getValue().getId();
        return selectedRecipe.getValue().nextStep(currentStepId);
    }

    public Step getPrevStep() {
        if (selectedStep.getValue() == null || selectedRecipe.getValue() == null) return null;
        int currentStepId = selectedStep.getValue().getId();
        return selectedRecipe.getValue().prevStep(currentStepId);
    }
}
