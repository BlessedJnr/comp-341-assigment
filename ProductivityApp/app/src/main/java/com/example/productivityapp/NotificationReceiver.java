package com.example.productivityapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    static String user = "bn@gmail,com";
    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference dbRef = database.getReference("Users").child(user).child("Projects");

    public static List<CreateProject> projects = new ArrayList<>();
    public static List<CreateTasks> overdueTasks = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        //setup your notification here

        NotificationHelper noti = new NotificationHelper
                (context,
                        "Overdue Tasks",
                        "Task 1 for Projec 3 is overdue is 3 days overdue",
                        0);

        noti.makeNotification();

        for (CreateTasks task : overdueTasks) {
            String taskName = task.getTask();
            String project = task.getProject();

            NotificationHelper notification = new NotificationHelper
                    (context,
                            "Overdue Tasks",
                            taskName + " for Project " + project + " is overdue is overdue",
                            0);

            noti.makeNotification();
        }


        Log.d("iiv", "Notification Broadcast recieved ");
    }

//    public static void getOverdueTasks() {
//        for (CreateProject project : projects) {
//
//            List<CreateTasks> tasks = project.getTasksList();
//            for (CreateTasks task : tasks) {
//                if (task.isOverdue())
//                    overdueTasks.add(task);
//            }
//        }
//    }

    public static void getProjects() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CreateProject project = snap.getValue(CreateProject.class);
                    projects.add(project);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("iiv", "Data sourcing cancelled");
            }
        });
    }
}
