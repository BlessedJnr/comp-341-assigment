package com.example.productivityapp.Project;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.R;
import com.google.android.gms.tasks.Tasks;
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

        if (calendar.before(currentCalendar)){
            holder.taskDueDate.setTextColor(Color.parseColor("#bf1919"));

        }

        //check if task is done
        if (tasks.getState().equals("Complete")){
            holder.checkTask.setChecked(true);
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.disabled));
            holder.cardView.setAlpha(0.7f);
            holder.taskName.setPaintFlags(holder.taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }

        else {
            holder.checkTask.setChecked(false);
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
}