package com.example.bakingapp.util;

import com.example.bakingapp.data.RecipeService;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InjectorUtils {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static Repository provideRepository() {
        return Repository.getInstance();
    }

    public static SharedViewModelFactory provideSharedViewModelFactory() {
        return new SharedViewModelFactory(provideRepository());
    }

    public static RecipeService provideRecipeService() {
        return provideRetrofit().create(RecipeService.class);
    }

    private static Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
