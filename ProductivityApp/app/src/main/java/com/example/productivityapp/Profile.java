package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.productivityapp.Login.Login;
import com.example.productivityapp.databinding.ActivityProfileBinding;
import com.example.productivityapp.databinding.ActivityProjectBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Profile extends AppCompatActivity {

    FirebaseAuth mAuth ;
    FirebaseUser currentUser ;

    ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //when signed in
        if (currentUser != null) {
            //get user details
            String username = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            binding.usernameT.setText(username);
            binding.usernameinput.setText(username);
            binding.emailinput.setText(email);

            //make inputs editable
            binding.editbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.usernameinput.setEnabled(true);
                }
            });

        }

        else {
            Intent intent = new Intent(Profile.this, Login.class);
            startActivity(intent);
        }
    }
}