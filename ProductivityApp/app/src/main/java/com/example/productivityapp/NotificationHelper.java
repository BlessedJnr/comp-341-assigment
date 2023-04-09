package com.example.productivityapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

public class NotificationHelper {
    private static final String CHANNEL_ID = "com.example.productivityapp.CHANNEL1";
    private static final String GROUP_OVERDUE = "com.example.productivityapp.OVERDUE";

    private Context context;
    private String title = "";
    private String contentText = "";
    private int notificationId;
    NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context, String title, String contentText, int notificationId) {
        this.title = title;
        this.contentText = contentText;
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
        this.notificationId = notificationId;
    }

    public void makeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_pending_actions_24)
                .setContentTitle(title)
                .setContentText(contentText)
                .setGroup(GROUP_OVERDUE)
                .setContentIntent(getProjectsPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private PendingIntent getProjectsPendingIntent(){
        Intent projectIntent = new Intent(context, ProjectActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(projectIntent);
        PendingIntent projectPendingIntent = stackBuilder.getPendingIntent
                (100,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        return projectPendingIntent;
    }
}
