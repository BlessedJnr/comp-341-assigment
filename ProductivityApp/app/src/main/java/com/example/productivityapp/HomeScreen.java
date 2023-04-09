package com.example.productivityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.Project.ProjectActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class HomeScreen extends BottomNavigationActivity {
    TextView textView;

    RecyclerView recyclerView;
    ArrayList<String> projectName,tasks, team;
    MyAdapter adapter;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_home_screen);
        textView = findViewById(R.id.textView);
        textView.setText("Home");


        projectName = new ArrayList<>();
        tasks = new ArrayList<>();
        team = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new MyAdapter(this, projectName,tasks,team);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displaydata();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.home); // Set the selected item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigateToActivity(item.getItemId());
                return true;
            }
        });



    }

    private void displaydata() {
        projectName.add("ProductivityApp");
        tasks.add("Wireframes,Research");
        team.add("Tawidza");
    }

}
