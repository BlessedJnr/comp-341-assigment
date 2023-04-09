package com.example.productivityapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.productivityapp.MainActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ActivitySignupBinding binding;
    DatabaseReference currentUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentUserDatabaseRef = database.getReference("All Users").child("Emails");
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser () {

        //get username and details
        String username = binding.usernameinput.getText().toString();
        String email = binding.emailinput.getText().toString();
        String password = binding.passwordinput.getText().toString();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            binding.emailinput.setError("User already exists");
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                user.updateProfile(profileUpdates);

                                //add user to the database
                                CreateUser createUser = new CreateUser(username, email);
                                addUserToDatabase(createUser);

                                //open the home
                                Intent intent = new Intent (Signup.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(Signup.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void addUserToDatabase (CreateUser createUser) {
        currentUserDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference newUserEntry = currentUserDatabaseRef.push();
                newUserEntry.setValue(createUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Signup.this, "User not added", Toast.LENGTH_SHORT).show();
            }
        });
    }


}