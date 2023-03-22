package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.example.productivityapp.databinding.ActivityTaskBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private ActivityTaskBinding binding;
    private RecyclerView taskRecyclerView;
    private List<TaskAdapter.MyTasks> taskItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void onCreateTaskItem () {
        taskItems = new ArrayList<>();
        taskItems.add(new TaskAdapter.MyTasks("Create other tasks","18 Mar"));
    }
    private void buildRecyclerView (){
        taskRecyclerView = binding.taskRecyclerView;
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TaskAdapter adapter = new TaskAdapter(taskItems, 15);
        taskRecyclerView.setAdapter(adapter);
    }
}