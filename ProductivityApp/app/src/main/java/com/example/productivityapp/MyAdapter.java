package com.example.productivityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHoldder> {
    private Context context;
    private ArrayList projectName,tasks,team;

    public MyAdapter(Context context, ArrayList projectName, ArrayList tasks, ArrayList team) {
        this.context = context;
        this.projectName = projectName;
        this.tasks = tasks;
        this.team = team;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHoldder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recentprojects,parent,false);
        return new MyViewHoldder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHoldder holder, int position) {
        holder.projectName.setText(String.valueOf(projectName.get(position)));
        holder.tasks.setText(String.valueOf(tasks.get(position)));
        holder.team.setText(String.valueOf(team.get(position)));


    }

    @Override
    public int getItemCount() {
        return projectName.size();
    }

    public class MyViewHoldder extends RecyclerView.ViewHolder {
        TextView projectName, tasks, team;
        public MyViewHoldder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.txtname);
            tasks= itemView.findViewById(R.id.tname);
            team = itemView.findViewById(R.id.names);
        }
    }
}
