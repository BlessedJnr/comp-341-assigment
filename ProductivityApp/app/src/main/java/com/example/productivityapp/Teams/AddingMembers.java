package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.productivityapp.databinding.ActivityAddingMembersBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddingMembers extends AppCompatActivity {
    TextInputEditText name,email,team,project;
    Button add;
    DatabaseReference teamMemberRef;
    ActivityAddingMembersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddingMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name = binding.addnameinput;
        email = binding.addemailinput;
        team = binding.addteamnameinput;
        project = binding.addprojectnameinput;
        add = binding.add;

        //get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //get the user email
        String email = user.getEmail();
        String encodedEmail = email.replace(".", ",");

        teamMemberRef = FirebaseDatabase.getInstance().getReference().child("All Teams").child(encodedEmail).child("Teams");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
                Intent intent = new Intent(AddingMembers.this, TeamsActivity.class);
                startActivity(intent);

            }
        });

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

                    if (index == -1){
                        Post post = new Post(email.getText().toString());
                        createTeams.getMembers().add(post);
                        DatabaseReference teamsRef = dataSnapshot.getRef();
                        teamsRef.setValue(createTeams);
                        Toast.makeText(AddingMembers.this, "Added", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddingMembers.this, "Failed to add members", Toast.LENGTH_SHORT).show();
            }
        });

    }

}