package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.productivityapp.databinding.EditDescriptionBinding;

public class EditDescriptionActivity extends AppCompatActivity {

    EditDescriptionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.taskDescription.setText(getIntent().getStringExtra("task_description"));
    }
}