package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityIndividualTaskBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IndividualTask extends AppCompatActivity {


    ActivityIndividualTaskBinding binding;
    private String taskName;
    private String projectName;
    private TextView projectNameTxt;
    private TextInputEditText taskNameTxt;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    //get the currently logged in user name
    private FirebaseAuth auth;
    private FirebaseUser user;
    DatabaseReference currentUserProjectRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndividualTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        String email = user.getEmail();
        String encodedEmail = email.replace(".", ",");
        currentUserProjectRef = usersRef.child(encodedEmail).child("Projects");

        //get task name from task activity
        taskName = getIntent().getStringExtra("taskName");
        //get the project name from task activity
        projectName = getIntent().getStringExtra("projectName");
        //set the task name field
        taskNameTxt = binding.taskinput;
        taskNameTxt.setText(taskName);
        //set the project name field
        projectNameTxt = binding.projectNameTitle;
        projectNameTxt.setText(projectName);


        String [] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.task_state, states);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);
    }
}