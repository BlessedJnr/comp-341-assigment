package com.example.productivityapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.CreateTasks;
import com.example.productivityapp.Project.ProjectActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of App Widget functionality.
 */
public class ProductivityWidget extends AppWidgetProvider {

    public static final String EXPAND_ACTION = "com.example.productivityapp.EXPAND_ACTION";
    public static final String EXTRA_PROJECT = "com.example.productivityapp.EXTRA_ITEM";
    public static final String COLLAPSE_ACTION = "com.example.productivityapp.COLLAPSE_ACTION";
    public static final String TOGGLE_STATE_ACTION = "com.example.productivityapp.TOGGLE_STATE_ACTION";
    public static final String EXTRA_TASK = "com.example.productivityapp.EXTRA_TASK";
    public static final String TOGGLE_DONE_ACTION = "com.example.productivityapp.TOGGLE_DONE_ACTION";


    public static String selectedProject = "none";

    static Context context;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);

        //Rendering the Projects
        Intent projectIntent = new Intent(context, ProjectWidgetService.class);
        projectIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        projectIntent.setData(Uri.parse(projectIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_lv_projects, projectIntent);

        Intent newProjectIntent = new Intent(context, ProjectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,newProjectIntent, PendingIntent.FLAG_MUTABLE);
        views.setOnClickPendingIntent(R.id.widget_btn_new, pendingIntent);

        //Onclick intent to change status of task
        Intent toggleStateIntent = new Intent(context, ProductivityWidget.class);
//        toggleStateIntent.setAction(ProductivityWidget.TOGGLE_STATE_ACTION);
        toggleStateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        toggleStateIntent.setData(Uri.parse(toggleStateIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent toggleStatePendingIntent = PendingIntent.getBroadcast(context, 0, toggleStateIntent, PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.widget_lv_tasks, toggleStatePendingIntent);

        //Pending intent for toggle done
//        Intent toggleDoneIntent = new Intent(context, ProductivityWidget.class);
//        toggleDoneIntent.setAction(TOGGLE_DONE_ACTION);
//        toggleDoneIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        toggleDoneIntent.setData(Uri.parse(toggleDoneIntent.toUri(Intent.URI_INTENT_SCHEME)));
//
//        PendingIntent toggleDonePendingIntent = PendingIntent.getBroadcast(context, 1, toggleDoneIntent, PendingIntent.FLAG_MUTABLE);
//        views.setOnClickPendingIntent(R.id.widget_lv_tasks, toggleDonePendingIntent);

        //Setting onclick to reshow projects
        Intent backIntent = new Intent(context, ProductivityWidget.class);
        backIntent.setAction(ProductivityWidget.COLLAPSE_ACTION);
        backIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent backPendingIntent = PendingIntent.getBroadcast(context, 0, backIntent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_btn_back, backPendingIntent);

        //Pending Intent to show task list
        Intent expandIntent = new Intent(context, ProductivityWidget.class);
        expandIntent.setAction(ProductivityWidget.EXPAND_ACTION);
        expandIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        projectIntent.setData(Uri.parse(projectIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent expandPendingIntent = PendingIntent.getBroadcast(context, 0, expandIntent,
                PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.widget_lv_projects, expandPendingIntent);

        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_lv_projects);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        this.context = context;
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        DatabaseReference dbRef = database.getReference("Users").child(email).child("Projects");

        if(intent.getAction().equals(EXPAND_ACTION)) {

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

//            int viewIndex = intent.getIntExtra(EXTRA_PROJECT, 0);
//            Toast.makeText(context, "Touched Project " + viewIndex, Toast.LENGTH_SHORT).show();

            String projectName = intent.getStringExtra(EXTRA_PROJECT);

            //show task list and hide Projects
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);
            views.setViewVisibility(R.id.widget_lv_projects, View.GONE);
            views.setViewVisibility(R.id.widget_tasks_container, View.VISIBLE);
            views.setTextViewText(R.id.widget_tv_tasks_projectname, projectName);
            selectedProject = projectName;

            //Rendering the list
            Intent taskIntent = new Intent(context, TaskWidgetService.class);
            taskIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            taskIntent.setData(Uri.parse(taskIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_lv_tasks, taskIntent);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ProductivityWidget.class.getName());
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);


            appWidgetManager.updateAppWidget(thisAppWidget, views);
        }

        if(intent.getAction().equals(COLLAPSE_ACTION)) {
            //Hide tasks and show Projects
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);
            views.setViewVisibility(R.id.widget_lv_projects, View.VISIBLE);
            views.setViewVisibility(R.id.widget_tasks_container, View.GONE);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ProductivityWidget.class.getName());

            selectedProject = "none";

            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);

            appWidgetManager.updateAppWidget(thisAppWidget, views);
        }

        if(intent.getAction().equals(TOGGLE_STATE_ACTION)){

            boolean taskInProgress = intent.getBooleanExtra("TASK_STATE", false);
            String projectKey = ProjectRemoteViewsFactory.projectKeysMap.get(selectedProject);
            String index = Integer.toString(intent.getIntExtra(EXTRA_TASK, 0));
            String tempProjectHolder = selectedProject;

            selectedProject = "none";
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);


            dbRef = dbRef.child(projectKey).child("tasksList").child(index);

            if(taskInProgress){
                dbRef.child("state").setValue("Pending");
                dbRef.child("inProgress").setValue(false);
            }
            else{
                dbRef.child("done").setValue(false);
                dbRef.child("state").setValue("In Progress");
                dbRef.child("inProgress").setValue(true);
            }

            selectedProject = tempProjectHolder;
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);


        }

        if(intent.getAction().equals(TOGGLE_DONE_ACTION)){

            int projectIndex = intent.getIntExtra(EXTRA_PROJECT, -1);
            int taskIndex = intent.getIntExtra(EXTRA_TASK, -1);

            boolean taskDone = intent.getBooleanExtra("TASK_DONE", false);
            String projectKey = ProjectRemoteViewsFactory.projectKeysMap.get(selectedProject);
            String index = Integer.toString(intent.getIntExtra(EXTRA_TASK, 0));
            String tempProjectHolder = selectedProject;

            selectedProject = "none";
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);


            dbRef = dbRef.child(projectKey).child("tasksList").child(index);

            if(taskDone){
                dbRef.child("state").setValue("Pending");
                dbRef.child("done").setValue(false);
                dbRef.child("inProgress").setValue(false);
            }
            else{
                dbRef.child("state").setValue("Complete");
                dbRef.child("done").setValue(true);
                dbRef.child("inProgress").setValue(false);
            }


            selectedProject = tempProjectHolder;
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_tasks);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }


    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void refreshWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ProductivityWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lv_projects);
    }
}