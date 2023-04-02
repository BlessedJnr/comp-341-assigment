package com.example.productivityapp;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class ProjectRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    List<String> projects;
    public ProjectRemoteViewsFactory(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {
        projects = new ArrayList<>();

        projects.add("Project 1");
        projects.add("Project 2");
        projects.add("Project 3");
        projects.add("Project 4");
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
