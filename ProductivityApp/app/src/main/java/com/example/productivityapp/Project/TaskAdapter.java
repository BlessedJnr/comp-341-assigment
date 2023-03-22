package com.example.productivityapp.Project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<MyTasks> mTaskList;
    private int mMargin;

    public TaskAdapter (List<MyTasks> tasklist, int margin){
        mTaskList = tasklist;
        mMargin = margin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        MyTasks tasks = mTaskList.get(position);
        holder.taskName.setText(tasks.getTaskName());
        holder.taskDueDate.setText(tasks.getTaskDueDate());

        // Set constraints for the card item view
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.topMargin = mMargin;
        layoutParams.bottomMargin = mMargin;

        if (position % 2 == 0) {
            layoutParams.leftMargin = mMargin;
            layoutParams.rightMargin = mMargin / 2;
        } else {
            layoutParams.leftMargin = mMargin / 2;
            layoutParams.rightMargin = mMargin;
        }
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() { return mTaskList.size();}


    public static class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView taskName;
        private TextView taskDueDate;

        public ViewHolder (View itemView){
            super(itemView);
            taskName = itemView.findViewById(R.id.task_name);
            taskDueDate = itemView.findViewById(R.id.task_due);
        }

    }

    public class MyTasks {
        private String taskName = "";
        private String taskDueDate = "";

        public MyTasks () {

        }

        public MyTasks (String name, String due){
            this.taskName = name;
            this.taskDueDate = due;
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
    }
}
