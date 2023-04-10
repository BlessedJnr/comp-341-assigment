package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.productivityapp.Login.CreateUser;
import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.ProjectAdapterClass;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityAddingMembersBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddingMembers extends AppCompatActivity {
    TextInputEditText name,email,team;
    Button add;
    DatabaseReference teamMemberRef, currentProjectRef, userRef;
    ActivityAddingMembersBinding binding;
    private ArrayList<String> projectList = new ArrayList<>();
    private boolean userAlreadyExists = false;
    private boolean isOwner = true;
    private String userName = "";
    String encodedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddingMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = binding.addemailinput;
        team = binding.addteamnameinput;
        add = binding.add;

        team.setText(getIntent().getStringExtra("teamName"));
        binding.autoCompleteTextView.setHint(getIntent().getStringExtra("projectName"));

        //get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //get the user email
        String userEmail = user.getEmail();
        encodedEmail = userEmail.replace(".", ",");

        //get references to the databases
        teamMemberRef = FirebaseDatabase.getInstance().getReference().child("All Teams").child(encodedEmail).child("Teams");
        currentProjectRef = FirebaseDatabase.getInstance().getReference("Users").child(encodedEmail).child("Projects");
        userRef = FirebaseDatabase.getInstance().getReference("All Users").child("Emails");


        retrieveProjects();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userExists();
            }
        });

    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    private void addMembers() {

        teamMemberRef.orderByChild("teamName").equalTo(team.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CreateTeams createTeams = dataSnapshot.getValue(CreateTeams.class);

                    boolean memberExists = false;

                    int index = -1;


                    for (int i = 0 ; i < createTeams.getMembers().size(); i++) {
                        if (createTeams.getMembers().get(i).getEmail().equals(email.getText().toString())){
                            index = i;
                            Toast.makeText(AddingMembers.this, "Member already exists", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    if (index == -1 && userAlreadyExists){
                        Post post = new Post(getUserName(),email.getText().toString());

                        addProjectToMember();
                        Toast.makeText(AddingMembers.this, "" + isOwner, Toast.LENGTH_SHORT).show();
                        if (isOwner) {
                            createTeams.getMembers().add(post);
                            DatabaseReference teamsRef = dataSnapshot.getRef();
                            teamsRef.setValue(createTeams);
                            addTeamToMember(createTeams);
                        }
                    }
                    else {
                        Toast.makeText(AddingMembers.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                redirectToTeams();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddingMembers.this, "Failed to add members", Toast.LENGTH_SHORT).show();
                redirectToTeams();
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
    private void userExists () {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CreateUser createUser = dataSnapshot.getValue(CreateUser.class);

                    if (createUser.getEmail().equals(email.getText().toString())) {
                        setUserName(createUser.getName());
                        userAlreadyExists = true;
                        addMembers(); // call addMembers() only after userExists() is complete
                        return;
                    }
                }
                Toast.makeText(AddingMembers.this, "User does not exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddingMembers.this, "Failure when checking the users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void redirectToTeams(){
        Intent intent = new Intent(AddingMembers.this, GetTeamMembers.class);
        intent.putExtra("teamName", getIntent().getStringExtra("teamName"));
        startActivity(intent);
    }
    public void setOwner(boolean owner) {
        isOwner = owner;
    }
    public boolean isOwner() {
        return isOwner;
    }

    private void addProjectToMember () {
        //query for existing projects with the same name
        currentProjectRef.orderByChild("projectName").equalTo(getIntent().getStringExtra("projectName")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //project with the same name exists
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                        if (createProject.getMainOwner().equals(encodedEmail)){
                            //remove the . in the member's email
                            String memberEncodedEmail = email.getText().toString().replace(".", ",");
                            //add the collaborated project to the members projects
                            DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("Users").child(memberEncodedEmail).child("Projects");
                            memberRef.push().setValue(createProject);
                            break;
                        }
                        else {
                            Toast.makeText(AddingMembers.this, "You don't own this team", Toast.LENGTH_SHORT).show();
                            setOwner(false);
                        }


                    }
                } else {
                    //no project with the same name exists
                    Toast.makeText(AddingMembers.this, "No such project", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTeamToMember (CreateTeams createTeams) {
        String memberEncodedEmail = email.getText().toString().replace(".", ",");
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference().child("All Teams").child(memberEncodedEmail).child("Teams");
        teamRef.push().setValue(createTeams);
    }



}