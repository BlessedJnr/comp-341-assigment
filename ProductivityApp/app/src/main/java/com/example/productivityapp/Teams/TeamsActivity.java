package com.example.productivityapp.Teams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.productivityapp.Project.CreateProject;
import com.example.productivityapp.Project.ProjectAdapterClass;
import com.example.productivityapp.databinding.ActivityTeamsBinding;
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
import java.util.Objects;

public class TeamsActivity extends AppCompatActivity {

    private ViewTeamsAdapter mAdapter;
    private List<ViewTeamsAdapter.TeamsItem> teamsItems;
    ActivityTeamsBinding binding;

    private FirebaseUser user;
    DatabaseReference currentUserTeamRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //firebase
        //get the currently logged in user name
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("All Teams");
        String email = user.getEmail();
        assert email != null;
        String encodedEmail = email.replace(".", ",");
        currentUserTeamRef = usersRef.child(encodedEmail).child("Teams");


        super.onCreate(savedInstanceState);
        binding = ActivityTeamsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up the toolbar
        Toolbar toolbar = binding.teamstoolbar;
        setSupportActionBar(toolbar);

        //get the teams
        createTeamCardList();
        buildRecylerView();
        retrieveProject();

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.teamsStandardBtmSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        binding.floatingActionButton.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        binding.teamsInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                String text = Objects.requireNonNull(binding.teamsInputEditText.getText()).toString();

                if (!text.isEmpty()) {

                    //Create a new team
                    CreateTeams team = new CreateTeams(text);
                    addToDatabase(team);

                    binding.teamsInputEditText.setText("");

                    retrieveProject();
                    hideKeyboard();
                    // Add a delay of 100ms before hiding the bottom sheet
                    new Handler().postDelayed(() -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN), 100);
                }
                return true;
            }
            return false;
        });
    }


    private void createTeamCardList(){
        teamsItems = new ArrayList<>();
    }
    private void buildRecylerView () {
        RecyclerView mRecylerView = binding.recyclerView;
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new ViewTeamsAdapter(teamsItems, TeamsActivity.this);
        mRecylerView.setAdapter(mAdapter);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addToDatabase(CreateTeams teams) {
        //push new project object
        DatabaseReference newTeamRef = currentUserTeamRef.push();
        newTeamRef.setValue(teams);
    }

    private void retrieveProject() {
        currentUserTeamRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear the list of teams
                teamsItems.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get the teams object from the snapshot
                    CreateTeams teams = dataSnapshot.getValue(CreateTeams.class);

                    //add project to the list of project items
                    assert teams != null;
                    teamsItems.add(new ViewTeamsAdapter.TeamsItem(teams.getTeamName()));

                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}