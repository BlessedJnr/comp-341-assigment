package com.example.productivityapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.CreateTasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    FirebaseDatabase database;
    DatabaseReference dbRef;
    static Context context;
    List<String> projects;
    public static Map<String, List<CreateTasks>> projectTasksMap;
    public static Map<String, String> projectKeysMap;
    private static int appWidgetId;

    public ProjectRemoteViewsFactory(Context context, Intent intent) {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        this.context = context;
    }

    @Override
    public void onCreate() {

        projects = new ArrayList<>();
        projectTasksMap = new HashMap<>();
        projectKeysMap = new HashMap<>();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Users").child("omonrizu@vmail,com").child("Projects");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projects.clear();
                projectTasksMap.clear();
                projectKeysMap.clear();
                refreshWidget();
                for(DataSnapshot snap : snapshot.getChildren()){

                    //get project tasks
                    List<CreateTasks> tasks = new ArrayList<>();
                    for(DataSnapshot taskSnap : snap.child("tasksList").getChildren()){
                        CreateTasks task = taskSnap.getValue(CreateTasks.class);
                        tasks.add(task);
                    }

                    CreateProject project = snap.getValue(CreateProject.class);
                    projects.add(project.getProjectName());
                    projectTasksMap.put(project.getProjectName(), tasks);
                    projectKeysMap.put(project.getProjectName(), snap.getKey().toString());
                }

                refreshWidget();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void refreshWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_projects);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);
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
        extras.putString(ProductivityWidget.EXTRA_PROJECT, projects.get(i));
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
