package com.example.productivityapp.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityInboxNotifitcationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class InboxNotifitcations extends BottomNavigationActivity {

    ActivityInboxNotifitcationsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxNotifitcationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup up the toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        //handle bottom navigation clicks
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.notifications); // Set the selected item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigateToActivity(item.getItemId());
                return true;
            }
        });
    }
}