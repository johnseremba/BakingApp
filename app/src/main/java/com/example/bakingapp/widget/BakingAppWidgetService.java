package com.example.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class BakingAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingAppViewsFactory(this.getApplicationContext(), intent);
    }
}