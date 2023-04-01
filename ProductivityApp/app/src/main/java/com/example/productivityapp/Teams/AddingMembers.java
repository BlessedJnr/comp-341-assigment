package com.example.productivityapp.Teams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.productivityapp.databinding.ActivityAddingMembersBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingMembers extends AppCompatActivity {
    TextInputEditText name,email,team,project;
    Button add;
    DatabaseReference TeamMembers;
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

        TeamMembers = FirebaseDatabase.getInstance().getReference().child("TeamMembers").child(encodedEmail);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
            }
        });

    }

    private void addMembers() {

        String namevar = name.getText().toString();
        String emailvar = email.getText().toString();
        String teamvar = team.getText().toString();
        String projectvar = project.getText().toString();
        TeamMember teamA = new TeamMember(namevar,emailvar,teamvar,projectvar);

        TeamMembers.push().setValue(teamA);
        Toast.makeText(AddingMembers.this,"Team Member Added",Toast.LENGTH_SHORT).show();
    }
}