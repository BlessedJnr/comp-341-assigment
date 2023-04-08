package com.example.productivityapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TaskWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
//        int position = intent.getIntExtra(ProductivityWidget.EXTRA_PROJECT, -1);
        return new TaskRemoteViewsFactory(getApplicationContext(), intent);
    }
}
