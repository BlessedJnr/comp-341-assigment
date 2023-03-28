package com.example.productivityapp.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityIndividualTaskBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IndividualTask extends AppCompatActivity {


    ActivityIndividualTaskBinding binding;
    private String taskName;
    private String projectName;
    private TextView projectNameTxt, dueDateTxt, descInputTxt;
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

        //bind other elements
        dueDateTxt = binding.displaydate;
        descInputTxt = binding.descinput;

        retrieveFromDatabase();

        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.task_state, states);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);

        binding.savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text fields
                String updatedTask = taskNameTxt.getText().toString();
                String updatedDescription = descInputTxt.getText().toString();
                String updatedDate = dueDateTxt.getText().toString();
                String state = binding.autoCompleteTextView.getText().toString();

                saveToDatabase(updatedTask, updatedDescription, updatedDate, state);
                Intent intent = new Intent(IndividualTask.this, TaskActivity.class);
                intent.putExtra("projectName",projectName);
                startActivity(intent);
            }
        });
    }

    private void retrieveFromDatabase (){
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < createProject.getTasksList().size(); i++) {
                        if (createProject.getTasksList().get(i).getTask().equals(taskName)) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1){
                        dueDateTxt.setText(createProject.getTasksList().get(index).getDueDate());
                        descInputTxt.setText(createProject.getTasksList().get(index).getDescription());
                        binding.taskState.setHint(createProject.getTasksList().get(index).getState());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToDatabase(String task, String desc, String date, String state) {
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < createProject.getTasksList().size(); i++) {
                        if (createProject.getTasksList().get(i).getTask().equals(taskName)) {
                            Toast.makeText(getApplicationContext(), createProject.getTasksList().get(i).getTask() + ": " + i, Toast.LENGTH_SHORT).show();
                            index = i;
                            break;
                        }
                    }

                    if (index != -1) {
                        CreateTasks updatedTask = new CreateTasks(projectName, task, desc, date, state);
                        createProject.getTasksList().set(index, updatedTask);

                        //update the CreateProject object in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                        break;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error occured try later", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }




}