package com.example.productivityapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.example.productivityapp.Project.CreateTasks;

import java.util.ArrayList;
import java.util.List;

public class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    int projectPosition;

    List<CreateTasks> tasks;
    Intent intent;
    int widgetId;

    public TaskRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;

    }

    @Override
    public void onCreate() {

        tasks = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        projectPosition = ProductivityWidget.selectedProject;
        if (projectPosition != -1) {
            tasks = ProductivityWidget.projects.get(projectPosition).getTasksList();
        }


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_item);

        views.setTextViewText(R.id.widget_tv_tasks_projectname, tasks.get(i).getTask());
        views.setTextViewText(R.id.widget_tv_taskstatus, tasks.get(i).getState());
        views.setTextViewText(R.id.widget_tv_taskdue, tasks.get(i).getDueDate());

        int progressImg = tasks.get(i).isInProgress() ? R.drawable.ic_toggle_on : R.drawable.ic_toggle_off;
        int doneImg = tasks.get(i).isDone() ? R.drawable.ic_undo_done : R.drawable.ic_done;

        views.setImageViewResource(R.id.widget_btn_toggle, progressImg);
        views.setImageViewResource(R.id.widget_btn_done, doneImg);

        //fill intent for state onclick event
        Bundle extras = new Bundle();
        extras.putInt(ProductivityWidget.EXTRA_PROJECT, projectPosition);
        extras.putInt(ProductivityWidget.EXTRA_TASK, i);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);

        fillIntent.setAction(ProductivityWidget.TOGGLE_STATE_ACTION);

        views.setOnClickFillInIntent(R.id.widget_btn_toggle, fillIntent);


        //fill intent for done onclick event
        Intent doneFillIntent = new Intent();
        doneFillIntent.putExtras(extras);
        doneFillIntent.setAction(ProductivityWidget.TOGGLE_DONE_ACTION);
        views.setOnClickFillInIntent(R.id.widget_btn_done, doneFillIntent);

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
