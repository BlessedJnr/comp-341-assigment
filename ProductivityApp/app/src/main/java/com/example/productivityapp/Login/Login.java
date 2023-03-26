package com.example.productivityapp.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.productivityapp.MainActivity;
import com.example.productivityapp.R;
import com.example.productivityapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private Button loginBtn;
    private TextInputEditText emailInput, passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginBtn = binding.loginbtn;
        emailInput = binding.emailinput;
        passwordInput = binding.passwordinput;

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //check if user is signed in (non null) and update it accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startMain();
        }
    }



    private void loginUser (){

        Toast.makeText(getApplicationContext(),"Trying", Toast.LENGTH_SHORT).show();
        //get the email
        String emailTxt = emailInput.getText().toString();
        //get the password
        String passwordTxt = passwordInput.getText().toString();

        if (TextUtils.isEmpty(emailTxt)){
            emailInput.setError("Email cannot be empty");
            emailInput.requestFocus();
        }

        else if(TextUtils.isEmpty(passwordTxt)){
            passwordInput.setError("Password cannot be empty");
            passwordInput.requestFocus();
        }

        else {
            mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                startMain();
                            }
                            else {
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
    private void startMain () {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }

}