package com.example.productivityapp.Project;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityProjectBinding;
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
import java.util.List;
import java.util.Objects;

public class ProjectActivity extends BottomNavigationActivity {

    private ProjectAdapterClass mAdapter;
    private List<ProjectAdapterClass.ProjectItem> projectItems;
    private ActivityProjectBinding binding;
    private TextInputEditText inputEditText;

    private FirebaseUser user;
    DatabaseReference currentUserProjectRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //firebase
        //get the currently logged in user name
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");
        String email = user.getEmail();
        assert email != null;
        String encodedEmail = email.replace(".", ",");
        currentUserProjectRef = usersRef.child(encodedEmail).child("Projects");

        super.onCreate(savedInstanceState);
        //binding for project activity
        binding = ActivityProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        //set up the toolbar
        //get the projects from firebase
        createProjectCardList();
        buildRecyclerView();
        updateProjects();
        displayProjects();

        //bind items
        FloatingActionButton addProject = binding.floatingActionButton;
        inputEditText = binding.textInputEditText;

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

        //create bottom sheet ans
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.projectStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //display bottom sheet to add project
        addProject.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            addProject.hide();
        });

        binding.addProject.setOnClickListener(v -> {
            String text = Objects.requireNonNull(inputEditText.getText()).toString();

            if (!text.isEmpty()) {

                //Create a new project
                CreateProject project = new CreateProject(text, encodedEmail);
                addToDatabase(project);

                inputEditText.setText("");
                // Add a delay of 100ms before hiding the bottom sheet
                new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 100);
                addProject.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_search_menu_item, menu);
        MenuItem menuItem = menu.findItem(R.id.project_search_bar);
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
                    displayProjects();
                }
                else {
                    filteredProjects(newText);
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void createProjectCardList() {
        projectItems = new ArrayList<>();
    }

    private void buildRecyclerView() {
        RecyclerView mRecyclerView = binding.recyclerView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new ProjectAdapterClass(projectItems, ProjectActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addToDatabase(CreateProject project) {
        //query for existing projects with the same name
        currentUserProjectRef.orderByChild("projectName").equalTo(project.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //project with the same name already exists, show an error message
                    Toast.makeText(getApplicationContext(), "A project with the same name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    //no project with the same name exists, add the new project
                    DatabaseReference newProjectRef = currentUserProjectRef.push();
                    newProjectRef.setValue(project);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filteredProjects(String searchedProject) {
        projectItems.clear();

        // create a new query object with the filter condition
        Query query = currentUserProjectRef.orderByChild("projectName")
                .startAt(searchedProject)
                .endAt(searchedProject + "\uf8ff");

        // attach a listener to retrieve the filtered data
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // process the retrieved data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //get the project object from the snapshot
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);

                    //add project to the list of project items
                    assert project != null;
                    projectItems.add(new ProjectAdapterClass.ProjectItem(project.getProjectName()));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProjects() {
        currentUserProjectRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list of project items

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the project object from the snapshot
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);

                    if (project.getCollaborated()){
                        getUpdatedProject(project);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getUpdatedProject(CreateProject createProject) {

        DatabaseReference collaboratedProjectRef = FirebaseDatabase.getInstance().getReference("Collaborated Teams").child(createProject.getMainOwner());
        collaboratedProjectRef.orderByChild("projectName").equalTo(createProject.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list of project items

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the project object from the snapshot
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);
                    updateProjects(project);
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateProjects (CreateProject updatedProject){
        currentUserProjectRef.orderByChild("projectName").equalTo(updatedProject.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //update the CreateProject object in the database
                    DatabaseReference projectRef = dataSnapshot.getRef();
                    projectRef.setValue(updatedProject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayProjects() {

        currentUserProjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                projectItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject project = dataSnapshot.getValue(CreateProject.class);
                    if (project != null) {
                        projectItems.add(new ProjectAdapterClass.ProjectItem(project.getProjectName()));
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}