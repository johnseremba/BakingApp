package com.example.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.bakingapp.R;

public class BakingAppWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.example.bakingapp.widget.EXTRA_ITEM";
    public static final String INGREDIENT_ACTION = "com.example.bakingapp.widget.INGREDIENT_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(INGREDIENT_ACTION)) {
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched: " + viewIndex, Toast.LENGTH_SHORT).show();
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

            // Create fillInIntent template
            Intent fillInIntentTemplate = new Intent(context, BakingAppWidgetProvider.class);
            fillInIntentTemplate.setAction(INGREDIENT_ACTION);
            fillInIntentTemplate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent fillInPendingIntent = PendingIntent.getBroadcast(context,
                    0,
                    fillInIntentTemplate,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.list_widget_ingredients, fillInPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
