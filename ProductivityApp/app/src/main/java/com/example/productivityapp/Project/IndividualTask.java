package com.example.productivityapp.Project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityIndividualTaskBinding;

public class IndividualTask extends AppCompatActivity {


    ActivityIndividualTaskBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndividualTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String [] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.task_state, states);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);
    }
}