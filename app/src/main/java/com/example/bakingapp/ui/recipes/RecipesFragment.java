package com.example.bakingapp.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RecipesFragment extends Fragment {
    public static final String TAG = RecipesFragment.class.getSimpleName();
    private Unbinder unbinder;
    private RecipesFragmentViewModel viewModel;

    @BindView(R.id.recipes_recycler_view)
    RecyclerView recipesRecyclerView;

    public RecipesFragment() {
        // Required empty public constructor
    }

    public static RecipesFragment newInstance() {
        return new RecipesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        unbinder = ButterKnife.bind(this, view);
        viewModel = new ViewModelProvider(this).get(RecipesFragmentViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initUI() {
        // update toolbar
        ((MainActivity) getActivity()).udpateAppToolbar(getString(R.string.title_baking_time));

        // Populate RecyclerView
        RecipesAdapter adapter = new RecipesAdapter(recipe -> {
            Toast.makeText(getContext(), recipe.getName(), Toast.LENGTH_SHORT).show();
        });

        adapter.setRecipes(viewModel.getRecipes());
        recipesRecyclerView.setAdapter(adapter);
    }
}
