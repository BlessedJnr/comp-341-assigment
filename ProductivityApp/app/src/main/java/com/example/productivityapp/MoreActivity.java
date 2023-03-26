package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.productivityapp.Login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


public class MoreActivity extends AppCompatActivity {
    private Button backbutton;
    private Button teamsBtn;

    private  Button logoutbtn;

    private  Button profilebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);


        profilebtn=(Button)findViewById(R.id.accountbtn);
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,Profile.class));

            }
        });

        backbutton = (Button) findViewById(R.id.backbtn);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        teamsBtn = (Button) findViewById(R.id.teamsbtn);
        teamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoreActivity.this,RetrieveData.class));
            }
        });
        logoutbtn=findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MoreActivity.this, Login.class));//u have to change here to go to first login class
            }
        });



    }
}