package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth ;
    FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            String email = currentUser.getEmail();

            TextView textViewEmail = findViewById(R.id.emailacc);
            textViewEmail.setText(email);

        }
    }
}