package com.example.productivityapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class ProductivityWidget extends AppWidgetProvider {

    public static final String EXPAND_ACTION = "com.example.productivityapp.EXPAND_ACTION";
    public static final String EXTRA_ITEM = "com.example.productivityapp.EXTRA_ITEM";
    public static final String COLLAPSE_ACTION = "com.example.productivityapp.COLLAPSE_ACTION";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);

        //Rendering the Projects
        Intent projectIntent = new Intent(context, ProjectWidgetService.class);
        projectIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        projectIntent.setData(Uri.parse(projectIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_lv_projects, projectIntent);

        //Rendering the list
        Intent taskIntent = new Intent(context, TaskWidgetService.class);
        taskIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        taskIntent.setData(Uri.parse(taskIntent.toUri(Intent.URI_INTENT_SCHEME)));
        taskIntent.putExtra("EXTRA_PROJECT_POSITION", 1);
        views.setRemoteAdapter(R.id.widget_lv_tasks, taskIntent);

        //Setting onclick to reshow projects
        Intent backIntent = new Intent(context, ProductivityWidget.class);
        backIntent.setAction(ProductivityWidget.COLLAPSE_ACTION);
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

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals(EXPAND_ACTION)) {

            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched Project " + viewIndex, Toast.LENGTH_SHORT).show();

            //show task list and hide Projects
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);
            views.setViewVisibility(R.id.widget_lv_projects, View.GONE);
            views.setViewVisibility(R.id.widget_tasks_container, View.VISIBLE);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ProductivityWidget.class.getName());


            appWidgetManager.updateAppWidget(thisAppWidget, views);
        }

        if(intent.getAction().equals(COLLAPSE_ACTION)) {
            //Hide tasks and show Projects
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.productivity_widget);
            views.setViewVisibility(R.id.widget_lv_projects, View.VISIBLE);
            views.setViewVisibility(R.id.widget_tasks_container, View.GONE);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ProductivityWidget.class.getName());


            appWidgetManager.updateAppWidget(thisAppWidget, views);
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
}