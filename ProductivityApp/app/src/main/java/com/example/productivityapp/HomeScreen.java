package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> projectName,tasks, team;
    MyAdapter adapter;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_home_screen);
        projectName = new ArrayList<>();
        tasks = new ArrayList<>();
        team = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new MyAdapter(this, projectName,tasks,team);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        displaydata();




    }

    private void displaydata() {
        //populate with information from the database
        /**Cursor cursor = db.getdata();
        if (cursor.getCount() == 0)
        {
            Toast.makeText(HomeScreen.this,"no recent activities",Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            while (cursor.moveToNext()){
         **/
        projectName.add("ProductivityApp");
        tasks.add("Wireframes,Research");
        team.add("Tawidza");
           // }
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                // Handle menu item 1 click
                return true;
            case R.id.Projects:
                // Handle menu item 2 click
                return true;
            case R.id.notifications:
                // Handle menu item 3 click
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}