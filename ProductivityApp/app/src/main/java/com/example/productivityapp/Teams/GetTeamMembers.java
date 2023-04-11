package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.productivityapp.Navigation.BottomNavigationActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityGetTeamMembersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GetTeamMembers extends BottomNavigationActivity {

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
        binding.projectname.setText(getIntent().getStringExtra("projectName"));


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

        //handle bottom navigation clicks
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.more); // Set the selected item
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navigateToActivity(item.getItemId());
                return true;
            }
        });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetTeamMembers.this, AddingMembers.class);
                intent.putExtra("teamName", getIntent().getStringExtra("teamName"));
                intent.putExtra("projectName", getIntent().getStringExtra("projectName"));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.teamDelete:
                deleteTeam();
                openTeamActivity();
                return true;
            case R.id.teamExit:
                openTeamActivity();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void createMemberCardList () {
        memberItems = new ArrayList<>();
    }

    private void buildRecyclerView (){
        RecyclerView mRecylerView = binding.recyclerView;
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        adapter = new ViewMembersAdapter(memberItems, GetTeamMembers.this, getIntent().getStringExtra("projectName"));
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

    private void deleteTeam () {
        teamMemberRef.orderByChild("teamName").equalTo(getIntent().getStringExtra("teamName")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GetTeamMembers.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openTeamActivity(){
        Intent intent = new Intent(GetTeamMembers.this, TeamsActivity.class);
        startActivity(intent);
    }

}