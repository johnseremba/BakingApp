package com.example.bakingapp.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.util.InjectorUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final Object LOCK = new Object();
    private final RecipeService recipeService;
    private static Repository repository;

    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private Recipe widgetRecipe;

    private Repository(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public static Repository getInstance() {
        if (repository == null) {
            synchronized (LOCK) {
                repository = new Repository(InjectorUtils.provideRecipeService());
            }
        }
        return repository;
    }

    public LiveData<List<Recipe>> getRecipes() {
        MutableLiveData<List<Recipe>> recipeList = new MutableLiveData<>();
        Call<List<Recipe>> request = recipeService.getBakingData();
        request.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NotNull Call<List<Recipe>> call, @NotNull Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    List<Recipe> result = response.body();
                    recipeList.postValue(result);
                } else {
                    errorLiveData.postValue("No Data!");
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Recipe>> call, @NotNull Throwable t) {
                t.printStackTrace();
                errorLiveData.postValue(t.getMessage());
            }
        });
        return recipeList;
    }

    public LiveData<String> getErrors() {
        return errorLiveData;
    }

    // provide recipe to widget
    public Recipe getRecipeToShare() {
        return widgetRecipe;
    }

    // set recipe to serve to widget
    public void setWidgetRecipe(Recipe widgetRecipe) {
        this.widgetRecipe = widgetRecipe;
    }
}
