package com.example.bakingapp.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakingapp.data.Repository;

public class SharedViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Repository repository;

    public SharedViewModelFactory(Repository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeSharedViewModel(repository);
    }
}
