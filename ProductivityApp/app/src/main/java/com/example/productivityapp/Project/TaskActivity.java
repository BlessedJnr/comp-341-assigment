package com.example.productivityapp.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.example.productivityapp.databinding.ActivityTaskBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private ActivityTaskBinding binding;
    private TaskAdapter adapter;
    private RecyclerView taskRecyclerView;
    private FloatingActionButton addTask;
    private TextView projectNameTxt;
    private TextInputEditText taskTxt;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    //get the currently logged in user name
    private FirebaseAuth auth;
    private FirebaseUser user;
    DatabaseReference currentUserProjectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the project name
        String projectName = getIntent().getStringExtra("projectName");

        //firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        String email = user.getEmail();
        String encodedEmail = email.replace(".", ",");
        currentUserProjectRef = usersRef.child(encodedEmail).child("Projects");


        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        projectNameTxt = binding.projectNameTitle;
        projectNameTxt.setText(projectName);
        //create the tasks list
        List<TaskAdapter.MyTasks> taskItems = new ArrayList<>();
        buildRecyclerView(taskItems);

        //create a bottomsheet and make it hidden
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.taskStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //bind views
        addTask = binding.addTask;
        taskTxt = binding.taskInputEditText;


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        taskTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    String text = taskTxt.getText().toString();

                    if (!text.isEmpty()) {
                        //create a new task
                        CreateTasks tasks = new CreateTasks(text);
                        addToDatabase(tasks,text,projectName);

                        taskTxt.setText("");
                        hideKeyboard();
                        // Add a delay of 100ms before hiding the bottom sheet
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            }
                        }, 100);
                    }
                    return true;
                }
                return false;
            }
        });



    }
    private void buildRecyclerView ( List<TaskAdapter.MyTasks> arr){
        taskRecyclerView = binding.taskRecyclerView;
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(arr, 15);
        taskRecyclerView.setAdapter(adapter);
    }

    private void hideKeyboard (){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addToDatabase(CreateTasks tasks, String taskName, String projectName) {
        //retrieve the CreateProject object from the database using the project name
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //check if the task already exists in the project's task list
                    boolean taskExists = false;
                    for (CreateTasks task : createProject.getTasksList()) {
                        if (task.getTask().equals(taskName)) {
                            taskExists = true;
                            break;
                        }
                    }
                    //add the new task to the tasksList of the CreateProject object if it doesn't already exist
                    if (!taskExists) {
                        createProject.getTasksList().add(new CreateTasks(taskName));

                        //update the CreateProject object in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT);
            }
        });
    }



    private void retrieveTasks (){

    }
}