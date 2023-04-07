package com.example.productivityapp;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    int projectPosition;

    List<Task> tasks;
    public TaskRemoteViewsFactory(Context context, int position) {
        this.context = context;
        this.projectPosition = position;
    }

    @Override
    public void onCreate() {

        tasks = new ArrayList<>();

    }

    @Override
    public void onDataSetChanged() {
        tasks.add(new Task("Task 1", "pending", "21-aug-2022", false));
        tasks.add(new Task("Task 2", "in progress", "21-aug-2022", false));
        tasks.add(new Task("Task 3", "pending", "21-aug-2022", false));
        tasks.add(new Task("Task 4", "pending", "21-aug-2022", false));
        tasks.add(new Task("Task 5", "pending", "21-aug-2022", false));
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
        views.setTextViewText(R.id.widget_tv_tasks_projectname, tasks.get(i).getName());
        views.setTextViewText(R.id.widget_tv_taskstatus, tasks.get(i).getStatus());
        views.setTextViewText(R.id.widget_tv_taskdue, tasks.get(i).getDueDate());

        int progressImg = tasks.get(i).isInprogress() ? R.drawable.ic_toggle_on : R.drawable.ic_toggle_off;
        int doneImg = tasks.get(i).isDone() ? R.drawable.ic_undo_done : R.drawable.ic_done;

        views.setImageViewResource(R.id.widget_btn_toggle, progressImg);
        views.setImageViewResource(R.id.widget_btn_done, doneImg);

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
