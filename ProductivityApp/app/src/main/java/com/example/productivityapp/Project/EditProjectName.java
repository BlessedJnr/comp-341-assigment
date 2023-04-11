package com.example.productivityapp.Project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityEditProjectNameBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProjectName extends AppCompatActivity {

    ActivityEditProjectNameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProjectNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up the toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        binding.editProjectName.setText(getIntent().getStringExtra("projectName"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_description_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.description_edit_save:
                String updatedProjectName = binding.editProjectName.getText().toString();
                updateProjectName(updatedProjectName);
                Intent intent = new Intent(EditProjectName.this, TaskActivity.class);
                intent.putExtra("projectName", updatedProjectName);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateProjectName(String updatedProjectName) {
        //Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String encodedEmail = user.getEmail().replace(".", ",");
        DatabaseReference currentUserProjectRef = FirebaseDatabase.getInstance().getReference("Users").child(encodedEmail).child("Projects");

        String projectName = getIntent().getStringExtra("projectName");

        //retrieve the CreateProject object from the database using the project name
        currentUserProjectRef.orderByChild("projectName").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateProject createProject = dataSnapshot.getValue(CreateProject.class);
                    createProject.setProjectName(updatedProjectName);
                    assert createProject != null;

                    DatabaseReference projectRef = dataSnapshot.getRef();
                    projectRef.setValue(createProject);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}