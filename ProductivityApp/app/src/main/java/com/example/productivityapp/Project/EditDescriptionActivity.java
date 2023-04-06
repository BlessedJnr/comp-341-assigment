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
import com.example.productivityapp.databinding.EditDescriptionBinding;

public class EditDescriptionActivity extends AppCompatActivity {

    EditDescriptionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up the toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        binding.taskDescription.setText(getIntent().getStringExtra("task_description"));
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
                Intent intent = new Intent(EditDescriptionActivity.this, IndividualTask.class);
                intent.putExtra("updatedTaskDescription", binding.taskDescription.getText().toString());
                intent.putExtra("projectName", getIntent().getStringExtra("projectName"));
                intent.putExtra("taskName", getIntent().getStringExtra("taskName"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}