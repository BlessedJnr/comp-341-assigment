package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddingMembers extends AppCompatActivity {
    EditText name,email,team,project;

    Button add;

    DatabaseReference TeamMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_members);

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        team=findViewById(R.id.team);
        project=findViewById(R.id.project);
        add=findViewById(R.id.add);

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