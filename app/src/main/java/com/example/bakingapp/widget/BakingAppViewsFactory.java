package com.example.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakingapp.R;
import com.example.bakingapp.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

import static com.example.bakingapp.widget.BakingAppWidgetProvider.EXTRA_ITEM;

public class BakingAppViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int appWidgetId;
    private List<Ingredient> ingredientList = new ArrayList<>();

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
        Ingredient ingredient = ingredientList.get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_ingredient);
        remoteViews.setTextViewText(R.id.text_quantity, ingredient.getQuantity());
        remoteViews.setTextViewText(R.id.text_ingredient,
                mContext.getString(R.string.msg_ingredient, ingredient.getMeasure(), ingredient.getIngredient()));

        // A fill-intent will be used to fill in the pending intent template
        // that is set on the collection view in the Widget Provider
        Bundle extras = new Bundle();
        extras.putInt(EXTRA_ITEM, position);

        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);

        // Handle click events for a given item
        remoteViews.setOnClickFillInIntent(R.id.widget_item_ingredient, fillIntent);
        return remoteViews;
    }

    @Override
    public void onDataSetChanged() {
        for (int i = 0; i < 10; i++) {
            ingredientList.add(new Ingredient("1" + i, "K", "Chillie seasoning"));
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
