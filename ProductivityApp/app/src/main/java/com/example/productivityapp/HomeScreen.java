package com.example.productivityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

public class HomeScreen extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_home_screen);




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