package com.example.productivityapp.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}