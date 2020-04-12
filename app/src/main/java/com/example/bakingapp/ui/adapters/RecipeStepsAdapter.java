package com.example.bakingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.R;
import com.example.bakingapp.data.model.Step;
import com.example.bakingapp.ui.ItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {
    private List<Step> mSteps;
    private ItemClickListener<Step> mListener;

    public RecipeStepsAdapter(ItemClickListener<Step> clickListener) {
        this.mListener = clickListener;
    }

    @NonNull
    @Override
    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsAdapter.ViewHolder holder, int position) {
        holder.bind(mSteps.get(position));
    }

    @Override
    public int getItemCount() {
        return mSteps != null ? mSteps.size() : 0;
    }

    public void setSteps(List<Step> steps) {
        this.mSteps = steps;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_step_number)
        TextView stepNumber;

        @BindView(R.id.text_recipe_short_desc)
        TextView recipeShortDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Step step) {
            stepNumber.setText(Integer.toString(step.getId()));
            recipeShortDescription.setText(step.getShortDescription());
            itemView.setOnClickListener(v -> mListener.onClick(step));
        }
    }
}
