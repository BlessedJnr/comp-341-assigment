package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.productivityapp.databinding.ActivityEditProjectNameBinding;

public class EditProjectName extends AppCompatActivity {

    ActivityEditProjectNameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProjectNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.editProjectName.setText(getIntent().getStringExtra("projectName"));
    }
}