package com.example.productivityapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.productivityapp.Project.CreateProject;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProjectRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    FirebaseDatabase database;
    DatabaseReference dbRef;
    Context context;
    List<String> projects;
    private int appWidgetId;

    public ProjectRemoteViewsFactory(Context context, Intent intent) {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        this.context = context;
    }

    @Override
    public void onCreate() {

        projects = new ArrayList<>();
//        database = FirebaseDatabase.getInstance();
//        dbRef = database.getReference("Users").child("test@gmail,com").child("Projects");

        if(ProductivityWidget.projects != null) {
            for (CreateProject project : ProductivityWidget.projects) {
                projects.add(project.getProjectName());
            }
        }

    }

    @Override
    public void onDataSetChanged() {

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
        extras.putInt(ProductivityWidget.EXTRA_PROJECT, i);
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
