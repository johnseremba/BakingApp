package com.example.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakingapp.R;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Ingredient;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.util.InjectorUtils;

import java.util.List;

public class BakingAppViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int appWidgetId;
    private Repository repository = InjectorUtils.provideRepository();
    private List<Ingredient> ingredientList;

    public BakingAppViewsFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || ingredientList.size() == 0) {
            return null;
        }

        Ingredient ingredient = ingredientList.get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_ingredient);
        remoteViews.setTextViewText(R.id.text_widget_quantity, ingredient.getQuantity());
        remoteViews.setTextViewText(R.id.text_widget_ingredient,
                mContext.getString(R.string.msg_ingredient, ingredient.getMeasure(), ingredient.getIngredient()));
        return remoteViews;
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        Recipe recipes = repository.getRecipeToShare();
        ingredientList = recipes.getIngredients();
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public long getItemId(int position) {
        return ingredientList.get(position).hashCode();
    }

    @Override
    public int getCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }
}
