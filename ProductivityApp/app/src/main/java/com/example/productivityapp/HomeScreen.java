package com.example.productivityapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.ProjectActivity;
import com.example.productivityapp.Project.ProjectAdapterClass;
import com.example.productivityapp.Teams.RecentProjectsAdapter;
import com.example.productivityapp.databinding.ActivityHomeScreenBinding;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreen extends BottomNavigationActivity {

    private RecentProjectsAdapter mAdapter;
    private List<RecentProjectsAdapter.ProjectItem> projectItems;
    private ActivityHomeScreenBinding binding;
    private TextInputEditText inputEditText;

    private FirebaseUser user;
    DatabaseReference currentUserProjectRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firebase
        //get the currently logged in user name
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        String email = user.getEmail();
        assert email != null;
        String encodedEmail = email.replace(".", ",");
        currentUserProjectRef = usersRef.child(encodedEmail).child("Projects");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.home); // Set the selected item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigateToActivity(item.getItemId());
                return true;
            }
        });

        createProjectCardList();
        buildRecyclerView();
        displayProjects();



    }
    private void createProjectCardList() {
        projectItems = new ArrayList<>();
    }
    private void buildRecyclerView() {
        RecyclerView mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new RecentProjectsAdapter(projectItems, HomeScreen.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void displayProjects() {

        currentUserProjectRef.orderByChild("lastModified").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);
                    if (project != null) {
                        projectItems.add(new RecentProjectsAdapter.ProjectItem(project.getProjectName()));
                    }
                }
                Collections.reverse(projectItems); // sort by most recent
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
