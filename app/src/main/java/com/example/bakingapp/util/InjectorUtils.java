package com.example.bakingapp.util;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.example.bakingapp.data.RecipeService;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;

import org.jetbrains.annotations.Nullable;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InjectorUtils {
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    @Nullable
    private static CountingIdlingResource idlingResource;

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

    public static CountingIdlingResource provideIdlingResource() {
        return idlingResource;
    }

    @VisibleForTesting
    @NonNull
    public static CountingIdlingResource provideTestingIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new CountingIdlingResource("idling_resource");
        }
        return idlingResource;
    }
}
