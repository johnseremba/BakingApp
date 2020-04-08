package com.example.bakingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.ui.adapters.RecipesAdapter;
import com.example.bakingapp.ui.viewmodel.RecipeSharedViewModel;
import com.example.bakingapp.ui.viewmodel.SharedViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RecipesFragment extends Fragment {
    public static final String TAG = RecipesFragment.class.getSimpleName();
    private Unbinder unbinder;
    private RecipeSharedViewModel viewModel;
    private RecipesFragmentInteractionListener mListener;

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
        viewModel = new ViewModelProvider(requireActivity(),
                new SharedViewModelFactory(Repository.getInstance())).get(RecipeSharedViewModel.class);
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

    public void setListener(RecipesFragmentInteractionListener listener) {
        mListener = listener;
    }

    private void initUI() {
        // update toolbar
        ((MainActivity) requireActivity()).udpateAppToolbar(getString(R.string.title_baking_time));

        // Populate RecyclerView
        RecipesAdapter adapter = new RecipesAdapter(this::onClickRecipe);

        adapter.setRecipes(viewModel.getRecipes());
        recipesRecyclerView.setAdapter(adapter);
    }

    private void onClickRecipe(Recipe recipe) {
        viewModel.setSelectedRecipe(recipe);
        mListener.showRecipeStepsFragment();
    }

    public interface RecipesFragmentInteractionListener {
        void showRecipeStepsFragment();
    }
}
