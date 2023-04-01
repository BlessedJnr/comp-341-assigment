package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.productivityapp.Login.Login;
import com.example.productivityapp.databinding.ActivityMoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;


public class MoreActivity extends AppCompatActivity {

    private Button teamsBtn;
    private  Button logoutbtn;
    private  Button profilebtn;
    private TextView usernameTxt;
    private ActivityMoreBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get current user
        FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();

        profilebtn = binding.accountbtn;
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,Profile.class));

            }
        });


        teamsBtn = binding.teamsbtn;
        teamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoreActivity.this,ManageTeams.class));
            }
        });
        logoutbtn = binding.logoutbtn;
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MoreActivity.this, Login.class));//u have to change here to go to first login class
            }
        });

        usernameTxt = binding.usernme;

        //get username when signed in
        if (user != null){
            String name = user.getDisplayName();
            usernameTxt.setText(name);
        }






    }
}