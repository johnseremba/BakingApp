package com.example.bakingapp.util;

import com.example.bakingapp.data.Repository;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;

public class InjectorUtils {
    public static Repository provideRepository() {
        return Repository.getInstance();
    }

    public static SharedViewModelFactory provideSharedViewModelFactory() {
        return new SharedViewModelFactory(provideRepository());
    }
}
