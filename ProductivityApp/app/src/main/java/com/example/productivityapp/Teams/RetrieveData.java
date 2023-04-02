package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.productivityapp.More.Adapter;
import com.example.productivityapp.More.Post;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityRetrieveDataBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RetrieveData extends AppCompatActivity {


    ActivityRetrieveDataBinding binding;
    private RecyclerView recyclerView;
    private DatabaseReference teamsRef;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        //get a reference to the Teams node in the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference allTeamsRef = database.getReference("All Teams");

        //get the user email
        String email = user.getEmail();
        String encodedEmail = email.replace(".", ",");
        teamsRef = allTeamsRef.child(encodedEmail).child("Teams");


        super.onCreate(savedInstanceState);
        binding = ActivityRetrieveDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set the team name
        binding.teamname.setText(getIntent().getStringExtra("teamName"));

        recyclerView=binding.recycler;
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //retrieveTeamByName(getIntent().getStringExtra("teamName"));
        FirebaseRecyclerOptions<Post> context=new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(FirebaseDatabase.getInstance().getReference()
                        .child("TeamMembers")
                        .child(encodedEmail)
                        .orderByChild("teamName")
                        .equalTo(getIntent().getStringExtra("teamName")), Post.class)
                .build();
        adapter=new Adapter(context,this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void retrieveTeamByName (String name) {
        Query query = teamsRef.orderByChild("teamName").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //get the first team that matches the query
                    DataSnapshot teamSnapshot = snapshot.getChildren().iterator().next();

                    Toast.makeText(RetrieveData.this, "key: " + snapshot.getValue(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RetrieveData.this, "Team not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RetrieveData.this, "Error retrieving team", Toast.LENGTH_SHORT).show();
            }
        });
    }
}