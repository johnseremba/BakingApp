package com.example.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.bakingapp.MainActivity;
import com.example.bakingapp.R;
import com.example.bakingapp.data.Repository;
import com.example.bakingapp.data.model.Recipe;
import com.example.bakingapp.util.InjectorUtils;

public class BakingAppWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_WIDGET_RECIPE = "com.example.bakingapp.widget.EXTRA_WIDGET_RECIPE";
    private Repository mRepository = InjectorUtils.provideRepository();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            // Widget update broadcast
            ComponentName componentName = new ComponentName(context, BakingAppWidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetManager.getAppWidgetIds(componentName), R.id.list_widget_ingredients);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // Sets up the intent that starts the BackingAppWidgetService
            // which will provide views for the collection
            Intent intent = new Intent(context, BakingAppWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate RemoteViews object for the app widget layout
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setRemoteAdapter(R.id.list_widget_ingredients, intent);
            remoteViews.setEmptyView(R.id.list_widget_ingredients, R.id.text_widget_empty_view);

            // Set widget title to be the title of the selected recipe
            Recipe widgetRecipe = mRepository.getRecipeToShare();
            if (widgetRecipe != null) {
                remoteViews.setTextViewText(R.id.text_widget_recipe_title,
                        context.getString(R.string.title_recipe, widgetRecipe.getName()));
            }

            // Create an Intent to launch the MainActivity
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.putExtra(EXTRA_WIDGET_RECIPE, widgetRecipe);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.text_widget_recipe_title, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, BakingAppWidgetProvider.class));
        context.sendBroadcast(intent);
    }
}
