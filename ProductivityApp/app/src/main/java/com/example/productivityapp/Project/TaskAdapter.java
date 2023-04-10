package com.example.productivityapp.Project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.Notifications.CreateInboxNotifications;
import com.example.productivityapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<CreateTasks> mTaskList;
    private int mMargin;

    private Context context;
    private String projectName;

    public TaskAdapter (List<CreateTasks> tasklist, int margin, TaskActivity activity, String projectName){
        mTaskList = tasklist;
        mMargin = margin;
        this.context = activity;
        this.projectName = projectName;

    }

    public void setTasksList(ArrayList<CreateTasks> tasklist) {
        mTaskList = tasklist;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        CreateTasks tasks = mTaskList.get(position);
        holder.taskName.setText(tasks.getTask());
        holder.taskDueDate.setText(tasks.getDueDate());




        //check if task is done
        if (tasks.getState().equals("Complete")){
            holder.checkTask.setChecked(true);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
            holder.cardView.setAlpha(0.7f);
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        else {
            holder.checkTask.setChecked(false);

            //convert due date to calendar object
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date taskDate = null;
            try {
                taskDate = dateFormat.parse(tasks.getDueDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(taskDate);

            //check if overdue
            Calendar currentCalendar = Calendar.getInstance();

            //when overdue
            if (calendar.before(currentCalendar)){
                holder.taskDueDate.setTextColor(Color.parseColor("#bf1919"));

            }
            else {
                currentCalendar.add(Calendar.HOUR, 24);
                if (calendar.before(currentCalendar) && !tasks.isNotified()) {
                    addNotification(tasks.getTask(), "due 24 hours");
                    popUpNotification(tasks.getTask(), "due in 24 hours");
                    updateDatabaseNotified(tasks.getTask(), true);
                }
                else if (calendar.after(currentCalendar) && tasks.isNotified()) {
                    updateDatabaseNotified(tasks.getTask(), false);
                }
            }
        }

        // Set constraints for the card item view
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = mMargin;
        layoutParams.bottomMargin = mMargin;

        holder.itemView.setLayoutParams(layoutParams);

        //when card view is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();;
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent (context, IndividualTask.class);
                    String taskName = mTaskList.get(adapterPosition).getTask();
                    intent.putExtra("taskName", taskName);
                    intent.putExtra("projectName", projectName);
                    context.startActivity(intent);
                }
            }
        });

        //when checkbox is clicked
        holder.checkTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    updateDatabaseTask(holder.taskName.getText().toString(), true);
                }
                else {
                    updateDatabaseTask(holder.taskName.getText().toString(), false);
                }
            }
        });

    }

    @Override
    public int getItemCount() { return mTaskList.size();}


    public static class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView taskName;
        private TextView taskDueDate;
        private CheckBox checkTask;

        private MaterialCardView cardView;
        public ViewHolder (View itemView){
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            taskDueDate = itemView.findViewById(R.id.task_due);
            checkTask = itemView.findViewById(R.id.check_task);
            cardView = itemView.findViewById(R.id.card);
        }

    }

    public static class MyTasks {
        private String taskName = "";
        private String taskDueDate = "";
        private String taskState = "pending";

        public MyTasks () {

        }

        public MyTasks(String name){
            this.taskName = name;
            this.taskDueDate = "";
            this.taskState = "pending";
        }

        public MyTasks (String name, String due){
            this.taskName = name;
            this.taskDueDate = due;
            this.taskState = "pending";
        }
        public String getTaskName() {
            return taskName;
        }
        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }
        public String getTaskDueDate() {
            return taskDueDate;
        }
        public void setTaskDueDate(String taskDueDate) {
            this.taskDueDate = taskDueDate;
        }
        public String getTaskState() {
            return taskState;
        }
        public void setTaskState(String taskState) {
            this.taskState = taskState;
        }
    }

    //update the state of the task
    private void updateDatabaseTask(String task, boolean isChecked) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String encodedEmail = user.getEmail().replace(".", ",");
        DatabaseReference currentUserProjectRef = FirebaseDatabase.getInstance().getReference("Users").child(encodedEmail).child("Projects");
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < Objects.requireNonNull(createProject).getTasksList().size(); i++) {
                        if (createProject.getTasksList().get(i).getTask().equals(task)) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        //update the last modified
                        createProject.setLastModified(new Date().getTime());
                        //update the state
                        if (isChecked){
                            createProject.getTasksList().get(index).setState("Complete");
                        }
                        else {
                            createProject.getTasksList().get(index).setState("In Progress");
                            Intent intent = new Intent(context, TaskActivity.class);
                            intent.putExtra("projectName", projectName);
                            context.startActivity(intent);
                        }
                        //update the CreateProject object in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                        break;
                    }
                    else {
                        Toast.makeText(context, "Error occurred try later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNotification(String task, String notification) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String encodedEmail = user.getEmail().replace(".", ",");
        DatabaseReference currentInboxProjectRef = FirebaseDatabase.getInstance().getReference("Notifications").child(encodedEmail).child("Inboxes");

        currentInboxProjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (notification.equals("due 24 hours")) {
                    CreateInboxNotifications inboxNotification = new CreateInboxNotifications();
                    String uniqueId = task + "_" + inboxNotification.getTimeStamp();
                    CreateInboxNotifications inboxNotifications = new CreateInboxNotifications("Task due", task + " is due in 24 hours");

                    boolean isNewNotification = true;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        CreateInboxNotifications existingNotification = childSnapshot.getValue(CreateInboxNotifications.class);
                        if (existingNotification != null && existingNotification.getUniqueId().equals(uniqueId) && existingNotification.getMessageBody().equals("task")) {
                            isNewNotification = false;
                            break;
                        }
                    }

                    if (isNewNotification) {
                        currentInboxProjectRef.push().setValue(inboxNotifications);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //keep track of notified tasks
    private void updateDatabaseNotified(String taskName, boolean notified) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String encodedEmail = currentUser.getEmail().replace(".", ",");
            databaseReference.child("Users").child(encodedEmail).child("Projects")
                    .orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                                //get index of the task
                                int index = -1;

                                for (int i = 0; i < Objects.requireNonNull(createProject).getTasksList().size(); i++) {
                                    if (createProject.getTasksList().get(i).getTask().equals(taskName)) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index != -1 && !createProject.getCollaborated()) {
                                    createProject.getTasksList().get(index).setNotified(notified);
                                    //update the CreateProject object in the database
                                    DatabaseReference projectRef = dataSnapshot.getRef();
                                    projectRef.setValue(createProject);
                                    break;
                                }
                                else if (index != -1 && createProject.getCollaborated()) {
                                    createProject.getTasksList().get(index).setNotified(notified);
                                    updateDatabaseNotifiedCollaborated(createProject.getMainOwner(), createProject.getProjectName(), taskName, notified);
                                    DatabaseReference projectRef = dataSnapshot.getRef();
                                    projectRef.setValue(createProject);
                                    break;
                                }
                                else {
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                        }
                    });
        }
    }

    private void popUpNotification(String taskName, String notificationType) {
        // Define the notification channel ID and name
        String channelId = "my_channel_id";
        String channelName = "My Channel";

        // Create the notification channel
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Set the notification properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_alarm)
                .setContentTitle(taskName)
                .setContentText("Task " + notificationType)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
    private void updateDatabaseNotifiedCollaborated(String mainOwner, String project, String taskName, boolean notified) {
        DatabaseReference collaboratedProjectRef = FirebaseDatabase.getInstance().getReference("Collaborated Teams").child(mainOwner);

        collaboratedProjectRef.orderByChild("projectName").equalTo(project).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < Objects.requireNonNull(createProject).getTasksList().size(); i++) {
                        if (createProject.getTasksList().get(i).getTask().equals(taskName)) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        createProject.setLastModified(new Date().getTime());
                        createProject.getTasksList().get(index).setNotified(notified);
                        //update the CreateProject object in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                        break;
                    }
                    else {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }
