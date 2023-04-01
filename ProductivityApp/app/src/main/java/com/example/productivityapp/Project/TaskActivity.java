package com.example.productivityapp.Project;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.R;
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
import java.util.Objects;

public class TaskActivity extends AppCompatActivity {

    private ActivityTaskBinding binding;
    private TaskAdapter adapter;
    private TextInputEditText taskTxt;

    DatabaseReference currentUserProjectRef;
    //create the tasks list
    List<TaskAdapter.MyTasks> taskItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the project name
        String projectName = getIntent().getStringExtra("projectName");

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


        super.onCreate(savedInstanceState);
        binding = ActivityTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup up the toolbar
        Toolbar toolbar = binding.projecttasktoolbar;
        setSupportActionBar(toolbar);
        TextView projectNameTxt = binding.projectNameTitle;
        projectNameTxt.setText(projectName);
        //get the projects tasks
        buildRecyclerView(taskItems);
        retrieveTasks();

        //create a bottom-sheet and make it hidden
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(binding.taskStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //bind views
        FloatingActionButton addTask = binding.addTask;
        taskTxt = binding.taskInputEditText;


        addTask.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        taskTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND){
                String text = Objects.requireNonNull(taskTxt.getText()).toString();

                if (!text.isEmpty()) {
                    //create a new task
                    CreateTasks tasks = new CreateTasks(text, projectName);
                    addToDatabase(text,projectName);

                    taskTxt.setText("");
                    hideKeyboard();
                    // Add a delay of 100ms before hiding the bottom sheet
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 100);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.projectdelete:
                Toast.makeText(getApplicationContext(), "Delete clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.projectexit:
                Toast.makeText(getApplicationContext(), "Exit clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void buildRecyclerView ( List<TaskAdapter.MyTasks> arr){
        RecyclerView taskRecyclerView = binding.taskRecyclerView;
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(arr, 15, TaskActivity.this, getIntent().getStringExtra("projectName"));
        taskRecyclerView.setAdapter(adapter);
    }
    private void hideKeyboard (){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void addToDatabase(String taskName, String projectName) {
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);

                    //check if the task already exists in the project's task list
                    boolean taskExists = false;
                    assert createProject != null;
                    for (CreateTasks task : createProject.getTasksList()) {
                        if (task.getTask().equals(taskName)) {
                            taskExists = true;
                            break;
                        }
                    }
                    //add the new task to the tasksList of the CreateProject object if it doesn't already exist
                    if (!taskExists) {
                        createProject.getTasksList().add(new CreateTasks(taskName,projectName));

                        //update the CreateProject object in the database
                        DatabaseReference projectRef = dataSnapshot.getRef();
                        projectRef.setValue(createProject);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveTasks() {
        taskItems.clear();
        String projectName = getIntent().getStringExtra("projectName");

        //retrieve the CreateProject object from the database using the project name
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);
                    assert createProject != null;
                    List<CreateTasks> tasksList = createProject.getTasksList();
                    ArrayList<TaskAdapter.MyTasks> taskItems = new ArrayList<>();

                    //loop through the tasksList and add each task to the taskItems list
                    for (CreateTasks task : tasksList) {
                        TaskAdapter.MyTasks taskItem = new TaskAdapter.MyTasks(task.getTask());
                        taskItems.add(taskItem);
                    }
                    //set the taskItems list to the adapter and notify the adapter of the changes
                    adapter.setTasksList(taskItems);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


}