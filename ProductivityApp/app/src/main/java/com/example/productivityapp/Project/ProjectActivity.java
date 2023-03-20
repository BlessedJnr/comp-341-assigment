package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProjectAdapterClass mAdapter;
    private List<ProjectAdapterClass.ProjectItem> projectItems;
    private ActivityProjectBinding binding;
    private FloatingActionButton addProject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createProjectCardList();
        buildRecyclerView();
        addProject = binding.floatingActionButton;
    }
    private void createProjectCardList() {
        projectItems = new ArrayList<>();
        projectItems.add(new ProjectAdapterClass.ProjectItem(R.drawable.img,"Work"));
    }
    
    private void buildRecyclerView() {
        mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
        mAdapter = new ProjectAdapterClass(projectItems);
        mRecyclerView.setAdapter(mAdapter);
    }
}