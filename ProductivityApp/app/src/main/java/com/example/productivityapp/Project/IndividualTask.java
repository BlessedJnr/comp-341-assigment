package com.example.productivityapp.Project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityIndividualTaskBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class IndividualTask extends AppCompatActivity {
    ActivityIndividualTaskBinding binding;
    private String taskName;
    private String projectName;
    private TextView dueDateTxt;
    private TextView descInputTxt;
    private TextInputEditText taskNameTxt;
    private DatabaseReference currentUserProjectRef;
    private int year, monthOfYear, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndividualTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //firebase
        //get the currently logged in user name
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        assert user != null;
        String email = user.getEmail();
        assert email != null;
        String encodedEmail = email.replace(".", ",");
        currentUserProjectRef = usersRef.child(encodedEmail).child("Projects");

        //Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        //get task name from task activity
        taskName = getIntent().getStringExtra("taskName");
        //get the project name from task activity
        projectName = getIntent().getStringExtra("projectName");

        //set the task name field
        taskNameTxt = binding.taskInput;
        taskNameTxt.setText(taskName);

        //set the project name field
        TextView projectNameTxt = binding.projectNameTitle;
        projectNameTxt.setText(projectName);

        //bind other elements
        dueDateTxt = binding.displaydate;
        descInputTxt = binding.descinput;

        retrieveFromDatabase();

        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.task_state, states);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);


        binding.addduedate.setOnClickListener(v -> {
            //get the instance of a calendar
            final Calendar calendar;

            //getting the year, month, day
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            //creating a date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    IndividualTask.this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(cal.getTime());
                        binding.displaydate.setText(selectedDate);
                    },
                    year, monthOfYear, day);

            datePickerDialog.show();
        });

        descInputTxt.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Description", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(IndividualTask.this, EditDescriptionActivity.class);
            intent.putExtra("taskDescription", descInputTxt.getText().toString());
            startActivity(intent);
        });
        binding.savebtn.setOnClickListener(v -> {
            //get text fields
            String updatedTask = Objects.requireNonNull(taskNameTxt.getText()).toString();
            String updatedDescription = descInputTxt.getText().toString();
            String updatedDate = dueDateTxt.getText().toString();
            String state = binding.autoCompleteTextView.getText().toString();

            if (state.equals("")){
                state = binding.autoCompleteTextView.getHint().toString();
            }
            updateDatabaseTask(updatedTask, updatedDescription, updatedDate, state);
            openTaskActivity();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.taskDelete:
                Toast.makeText(getApplicationContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                deleteDatabaseTask();
                openTaskActivity();
                return true;
            case R.id.taskExit:
                openTaskActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void openTaskActivity (){
        Intent intent = new Intent(IndividualTask.this, TaskActivity.class);
        intent.putExtra("projectName",projectName);
        startActivity(intent);
    }
    private void retrieveFromDatabase (){
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < Objects.requireNonNull(createProject).getTasksList().size(); i++) {
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
    private void updateDatabaseTask(String task, String desc, String date, String state) {
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //get index of the task
                    int index = -1;

                    for (int i = 0; i < Objects.requireNonNull(createProject).getTasksList().size(); i++) {
                        if (createProject.getTasksList().get(i).getTask().equals(taskName)) {
                            Toast.makeText(getApplicationContext(), createProject.getTasksList().get(i).getTask() + ": " + i, Toast.LENGTH_SHORT).show();
                            taskName = task;
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
                        Toast.makeText(getApplicationContext(), "Error occurred try later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteDatabaseTask() {
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);
                    if (createProject != null && createProject.getTasksList() != null) {
                        for (CreateTasks task : createProject.getTasksList()) {
                            if (task.getTask().equals(taskName)) {
                                createProject.getTasksList().remove(task);
                                dataSnapshot.getRef().setValue(createProject);
                                Toast.makeText(getApplicationContext(), "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to Delete Task", Toast.LENGTH_SHORT).show();
            }
        });
    }

}