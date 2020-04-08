package com.example.bakingapp.ui.detail;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRecipeStepFragment extends Fragment {


    public ViewRecipeStepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_recipe_step, container, false);
    }

}
