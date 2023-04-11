package com.example.productivityapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.CreateTasks;
import com.example.productivityapp.Project.TaskActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationReceiver extends BroadcastReceiver {

    static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference dbRef = database.getReference("Users").child(email).child("Projects");

    private List<CreateTasks> overdueTasks = new ArrayList<>();

    private List<CreateTasks> tasksDueToday = new ArrayList<>();

    private Map<String, List<CreateTasks>> allProjectsMap = new HashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        populateProjectsMap(new CompletionListner() {
            @Override
            public void onComplete() {
                getOverdueTasks();

                int notificatinCounter = 1;
                for (CreateTasks task : overdueTasks) {
                    String taskName = task.getTask();
                    String project = task.getProject();
                    int daysDue = task.getDaysOverdue();

                    NotificationHelper notification = new NotificationHelper
                            (context,
                                    "Overdue Task",
                                    taskName + " from Project: " + project + " is " + daysDue + " days overdue",
                                    notificatinCounter,project, taskName);

                    notification.makeNotification();
                    notificatinCounter++;
                }
                for (CreateTasks task : tasksDueToday) {
                    String taskName = task.getTask();
                    String project = task.getProject();
                    int daysDue = task.getDaysOverdue();

                    NotificationHelper notification = new NotificationHelper
                            (context,
                                    "Task Due Today",
                                    taskName + " from Project: " + project + " is due today",
                                    notificatinCounter,project, taskName);

                    notification.makeNotification();
                    notificatinCounter++;
                }

            }
        });




        Log.d("iiv", "Notification Broadcast recieved ");
    }

    private void getOverdueTasks() {
        for (List<CreateTasks> tasks : allProjectsMap.values()) {

            for (CreateTasks task : tasks) {
                if (task.isOverdue() && task.getDaysOverdue() !=0)
                    overdueTasks.add(task);

                //gets tasks due today
                if (task.getDaysOverdue() == 0)
                    tasksDueToday.add(task);
            }
        }
        Log.d("iiv", "overdue tasks populated: " + overdueTasks);
    }

    private void populateProjectsMap(final CompletionListner completionListner) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                allProjectsMap.clear();
//                overdueTasks.clear();
//                tasksDueToday.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {

                    //get project tasks
                    List<CreateTasks> tasks = new ArrayList<>();
                    for (DataSnapshot taskSnap : snap.child("tasksList").getChildren()) {
                        CreateTasks task = taskSnap.getValue(CreateTasks.class);
                        tasks.add(task);
                    }

                    CreateProject project = snap.getValue(CreateProject.class);
                    allProjectsMap.put(project.getProjectName(), tasks);
                }

                Log.d("iiv", "project map populated: "+ allProjectsMap.toString());
                completionListner.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface CompletionListner{
        void onComplete();
    }
}