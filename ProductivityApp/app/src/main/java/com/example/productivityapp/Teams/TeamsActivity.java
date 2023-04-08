package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityTeamsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamsActivity extends AppCompatActivity {

    private ViewTeamsAdapter mAdapter;
    private List<ViewTeamsAdapter.TeamsItem> teamsItems;
    ActivityTeamsBinding binding;
    private FirebaseUser user;
    DatabaseReference currentUserTeamRef, currentProjectRef;
    private ArrayList<String> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //firebase
        //get the currently logged in user name
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("All Teams");
        String email = user.getEmail();
        assert email != null;
        String encodedEmail = email.replace(".", ",");

        //get other references to the real time database
        currentUserTeamRef = usersRef.child(encodedEmail).child("Teams");
        currentProjectRef = FirebaseDatabase.getInstance().getReference("Users").child(encodedEmail).child("Projects");


        super.onCreate(savedInstanceState);
        binding = ActivityTeamsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up the toolbar
        Toolbar toolbar = binding.teamstoolbar;
        setSupportActionBar(toolbar);

        //get the teams
        createTeamCardList();
        buildRecylerView();
        retrieveTeam();

        //get all the projects
        retrieveProjects();

        //add projects to dropdown menu
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.team_project, projectList);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.teamsStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                binding.floatingActionButton.hide();
            }
        });

        binding.addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = Objects.requireNonNull(binding.teamsInputEditText.getText()).toString();
                String selectedProject = binding.autoCompleteTextView.getText().toString();
                Toast.makeText(TeamsActivity.this, selectedProject, Toast.LENGTH_SHORT).show();
                if (!text.isEmpty()) {

                    //Create a new team
                    CreateTeams team = new CreateTeams(text, selectedProject);
                    addToDatabase(team);

                    binding.teamsInputEditText.setText("");

                    retrieveTeam();
                    // Add a delay of 100ms before hiding the bottom sheet
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 100);
                    binding.floatingActionButton.show();
                }
                else {
                    Toast.makeText(TeamsActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    binding.floatingActionButton.show();
                }
            }
        });
    }


    private void createTeamCardList(){
        teamsItems = new ArrayList<>();
    }
    private void buildRecylerView () {
        RecyclerView mRecylerView = binding.recyclerView;
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new ViewTeamsAdapter(teamsItems, TeamsActivity.this);
        mRecylerView.setAdapter(mAdapter);
    }
    private void addToDatabase(CreateTeams teams) {
        //push new team
        makeProjectCollaborated();
        DatabaseReference newTeamRef = currentUserTeamRef.push();
        newTeamRef.setValue(teams);
    }
    private void retrieveTeam() {
        currentUserTeamRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list of teams
                teamsItems.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the teams object from the snapshot
                    CreateTeams teams = dataSnapshot.getValue(CreateTeams.class);

                    //add project to the list of project items
                    assert teams != null;
                    teamsItems.add(new ViewTeamsAdapter.TeamsItem(teams.getTeamName(), teams.getProjectName()));

                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveProjects() {
        currentProjectRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the project object from the snapshot
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);

                    //add project to the list of project items
                    assert project != null;
                    projectList.add(project.getProjectName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void makeProjectCollaborated () {
        //query for existing projects with the same name
        currentProjectRef.orderByChild("projectName").equalTo(binding.autoCompleteTextView.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //project with the same name already exists, show an error message
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CreateProject createProject = dataSnapshot.getValue(CreateProject.class);
                        createProject.setCollaborated(true);

                        //update the project in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                        break;
                    }
                } else {
                    //no project with the same name exists, add the new project

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}