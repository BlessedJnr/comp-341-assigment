package com.example.productivityapp.Teams;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.productivityapp.R;

public class ManageTeams extends AppCompatActivity {


    private Button manage;
    private Button addmember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_teams);

        manage=(Button) findViewById(R.id.showTeams);
        addmember=(Button) findViewById(R.id.addMember);

        addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageTeams.this, AddingMembers.class));
            }
        });

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ManageTeams.this, RetrieveData.class));
            }
        });
    }
}