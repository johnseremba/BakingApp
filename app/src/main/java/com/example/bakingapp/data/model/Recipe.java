package com.example.bakingapp.data.model;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private String image;
    private int servings;
    private List<Ingredient> ingredients;
    private List<Step> steps;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
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

    public int numberOfSteps() {
        return steps != null ? steps.size() : 0;
    }

    public Step nextStep(int position) {
        try {
            return steps.get(++position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Step prevStep(int position) {
        try {
            return steps.get(--position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
