package com.example.bakingapp.data;

import com.example.bakingapp.data.model.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class Repository {
    public static final String PATH_TO_JSON_FILE = "res/raw/bakingapp.json";
    private static Repository repository;
    private static final Object LOCK = new Object();

    private List<Recipe> recipeList;

    private Repository() {
        recipeList = getRecipes();
    }

    public static Repository getInstance() {
        if (repository == null) {
            synchronized (LOCK) {
                repository = new Repository();
            }
        }
        return repository;
    }

    public List<Recipe> getRecipies() {
        return recipeList;
    }

    private List<Recipe> getRecipes() {
        String recipeJson = getRecipesFromJson();

        if (recipeJson == null || recipeJson.isEmpty()) return null;

        Gson gson = new Gson();
        // TypeToken assists us to find the correct Type for List
        Type recipeListType = new TypeToken<List<Recipe>>() {
        }.getType();
        return gson.fromJson(recipeJson, recipeListType);
    }

    private String getRecipesFromJson() {
        InputStream stream = Objects.requireNonNull(
                getClass().getClassLoader()).getResourceAsStream(PATH_TO_JSON_FILE);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
