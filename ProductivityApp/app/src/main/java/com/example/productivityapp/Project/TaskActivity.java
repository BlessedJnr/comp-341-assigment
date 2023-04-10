package com.example.productivityapp.Project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityTaskBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TaskActivity extends BottomNavigationActivity {

    private ActivityTaskBinding binding;
    private TaskAdapter adapter;
    private TextInputEditText taskTxt;

    DatabaseReference currentUserProjectRef;
    //create the tasks list
    List<CreateTasks> taskItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the project name
        String projectName = getIntent().getStringExtra("projectName");
        Boolean isCollaborated = getIntent().getBooleanExtra("collaborated", false);

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
        //get references to the databases
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

        //handle project name click
        projectNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, EditProjectName.class);
                intent.putExtra("projectName", projectName);
                startActivity(intent);
            }
        });

        //handle bottom navigation clicks
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.Projects); // Set the selected item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigateToActivity(item.getItemId());
                return true;
            }
        });

        //create a bottom-sheet and make it hidden
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(binding.taskStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //bind views
        FloatingActionButton addTask = binding.addTask;
        taskTxt = binding.taskInputEditText;


        addTask.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            addTask.hide();
        } );

        binding.createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = Objects.requireNonNull(taskTxt.getText()).toString();

                if (!text.isEmpty()) {
                    //create a new task
                    CreateTasks tasks = new CreateTasks(text, projectName);
                    addToDatabase(text,projectName);
                    taskTxt.setText("");
                    // Add a delay of 100ms before hiding the bottom sheet
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 100);
                    addTask.show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_options_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.task_search_bar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    retrieveTasks();
                }else {
                    retrieveFilteredTasks(newText);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.project_delete:
                deleteProject();
                openProjectsActivity();
                return true;
            case R.id.project_exit:
                openProjectsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void openProjectsActivity () {
        Intent intent = new Intent(getApplicationContext(), ProjectActivity.class);
        startActivity(intent);
    }
    private void buildRecyclerView ( List<CreateTasks> arr){
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
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        createProject.setLastModified(new Date().getTime());
                        createProject.getTasksList().add(new CreateTasks(taskName,projectName));
                        //update all references of the collaborated project
                        if (createProject.getCollaborated()){
                            updateCollaborated(createProject);
                        }
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
                    ArrayList<CreateTasks> tasksList = createProject.getTasksList();
                    adapter.setTasksList(tasksList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveFilteredTasks(String task) {
        String projectName = getIntent().getStringExtra("projectName");
        Query query = currentUserProjectRef.orderByChild("projectName").equalTo(projectName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<CreateTasks> filteredItems = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);
                    assert createProject != null;
                    ArrayList<CreateTasks> tasksList = createProject.getTasksList();

                    for (CreateTasks mTasks : tasksList) {
                        if (mTasks.getTask().contains(task)) {
                            filteredItems.add(mTasks);
                        }
                    }
                }

                adapter.setTasksList(filteredItems);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteProject() {
        String projectName = getIntent().getStringExtra("projectName");
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(TaskActivity.this, "Project Deleted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskActivity.this, "Failed to delete project", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateCollaborated(CreateProject updatedProject) {
        DatabaseReference collaboratedProjectRef = FirebaseDatabase.getInstance().getReference("Collaborated Teams").child(updatedProject.getMainOwner());
        collaboratedProjectRef.orderByChild("projectName").equalTo(updatedProject.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list of project items

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the project object from the snapshot
                    DatabaseReference projectRef = dataSnapshot.getRef();
                    projectRef.setValue(updatedProject);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}