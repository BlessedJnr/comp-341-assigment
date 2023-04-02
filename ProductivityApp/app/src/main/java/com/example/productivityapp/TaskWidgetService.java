package com.example.productivityapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int position = intent.getIntExtra("EXTRA_PROJECT_POSITION", -1);
        return new TaskRemoteViewsFactory(getApplicationContext(), position);
    }
}
