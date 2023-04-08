package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.productivityapp.databinding.ActivityGetTeamMembersBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GetTeamMembers extends AppCompatActivity {

    ActivityGetTeamMembersBinding binding;
    private List<Post> memberItems;
    private ViewMembersAdapter adapter;
    DatabaseReference teamMemberRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //get the user email
        String email = user.getEmail();
        String encodedEmail = email.replace(".", ",");
        teamMemberRef = FirebaseDatabase.getInstance().getReference().child("All Teams").child(encodedEmail).child("Teams");

        super.onCreate(savedInstanceState);

        binding = ActivityGetTeamMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.setText(getIntent().getStringExtra("teamName"));


        //setup the toolbar
        Toolbar toolbar = binding.memberstoolbar;
        setSupportActionBar(toolbar);

        //create a bottom-sheet and make it hidden
        BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(binding.memberStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        //get the members
        createMemberCardList();
        buildRecyclerView();
        retrieveMembers();

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GetTeamMembers.this, "Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GetTeamMembers.this, AddingMembers.class);
                intent.putExtra("teamName", getIntent().getStringExtra("teamName"));
                intent.putExtra("projectName", getIntent().getStringExtra("projectName"));
                startActivity(intent);
            }
        });

    }

    private void createMemberCardList () {
        memberItems = new ArrayList<>();
    }

    private void buildRecyclerView (){
        RecyclerView mRecylerView = binding.recyclerView;
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        adapter = new ViewMembersAdapter(memberItems, GetTeamMembers.this);
        mRecylerView.setAdapter(adapter);
    }

    private void retrieveMembers () {

        teamMemberRef.orderByChild("teamName").equalTo(getIntent().getStringExtra("teamName")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CreateTeams createTeams = dataSnapshot.getValue(CreateTeams.class);
                    ArrayList<Post> members = createTeams.getMembers();
                    adapter.setMemberList(members);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GetTeamMembers.this, "Failed to retrieve", Toast.LENGTH_SHORT).show();
            }
        });
    }

}