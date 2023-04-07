package com.example.productivityapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class ProjectRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    List<String> projects;
    private int appWidgetId;
    public ProjectRemoteViewsFactory(Context context, Intent intent){
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        this.context = context;
    }

    @Override
    public void onCreate() {
        projects = new ArrayList<>();


    }

    @Override
    public void onDataSetChanged() {
        projects.add("Project 1");
        projects.add("Project 2");
        projects.add("Project 3");
        projects.add("Project 8");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.project_item);
        views.setTextViewText(R.id.widget_tv_project, projects.get(i));

        Bundle extras = new Bundle();
        extras.putInt(ProductivityWidget.EXTRA_ITEM, i);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);

        views.setOnClickFillInIntent(R.id.widget_tv_project, fillIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
