package com.example.bakingapp.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.bakingapp.R;

public class ViewRecipeStepFragment extends Fragment {
    public static final String TAG = ViewRecipeStepFragment.class.getSimpleName();

    public ViewRecipeStepFragment() {
        // Required empty public constructor
    }

    public static ViewRecipeStepFragment getInstance() {
        return new ViewRecipeStepFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_recipe_step, container, false);
    }

}
