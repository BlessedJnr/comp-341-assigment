package com.example.productivityapp.Notifications;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityInboxNotifitcationsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InboxNotifitcations extends BottomNavigationActivity {

    ActivityInboxNotifitcationsBinding binding;
    private InboxNotificationsAdapter adapter;
    private List<CreateInboxNotifications> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInboxNotifitcationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup up the toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        buildRecyclerView(messages);
        retrieveNotifications();

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

    private void buildRecyclerView (List<CreateInboxNotifications>  messagesArr){
        RecyclerView notificationsRecyclerView = binding.recyclerView;
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InboxNotificationsAdapter(messagesArr, 15);
        notificationsRecyclerView.setAdapter(adapter);
    }

    private  void retrieveNotifications() {
        messages.clear();
        //firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String encodedEmail = user.getEmail().replace(".", ",");
        DatabaseReference currentInboxProjectRef = FirebaseDatabase.getInstance().getReference("Notifications").child(encodedEmail).child("Inboxes");

        currentInboxProjectRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CreateInboxNotifications createInboxNotifications = dataSnapshot.getValue(CreateInboxNotifications.class);
                    messages.add(createInboxNotifications);

                }
                Collections.reverse(messages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}