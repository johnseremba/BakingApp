package com.example.bakingapp.data.model;

import java.util.List;

public class Recipe {
    private int id;
    private String image;
    private String servings;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    public Recipe() {
    }

    public Recipe(int id, String image, String servings, List<Ingredient> ingredients, List<Step> steps) {
        this.id = id;
        this.image = image;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
