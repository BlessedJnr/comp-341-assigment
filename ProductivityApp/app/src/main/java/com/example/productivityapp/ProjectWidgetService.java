package com.example.productivityapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ProjectWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ProjectRemoteViewsFactory(getApplicationContext(), intent);
    }
}
