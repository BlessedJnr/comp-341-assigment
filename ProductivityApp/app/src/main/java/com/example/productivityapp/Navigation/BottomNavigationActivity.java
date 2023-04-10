package com.example.productivityapp.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.productivityapp.HomeScreen;
import com.example.productivityapp.More.MoreActivity;
import com.example.productivityapp.Notifications.InboxNotifitcations;
import com.example.productivityapp.Project.ProjectActivity;
import com.example.productivityapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
    }

    public void navigateToActivity(int itemId) {
        switch (itemId) {
            case R.id.home:
                Intent intentHome = new Intent(this, HomeScreen.class);
                startActivity(intentHome);
                break;
            case R.id.Projects:
                Intent intentProject = new Intent(this, ProjectActivity.class);
                startActivity(intentProject);
                break;
            case R.id.notifications:
                Intent intentNotifications = new Intent(this, InboxNotifitcations.class);
                startActivity(intentNotifications);
                break;
            case R.id.more:
                Intent intentMore = new Intent(this, MoreActivity.class);
                startActivity(intentMore);
                break;
            default:
                navigateToActivity(R.id.home);
        }
    }

}
