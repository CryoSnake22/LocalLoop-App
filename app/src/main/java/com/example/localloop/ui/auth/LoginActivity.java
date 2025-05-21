package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailField, passwordField;
    private Button loginButton, signupButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.inputEmail);
        passwordField = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signupButton = findViewById(R.id.buttonSignup);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0")) {
                setContentView(R.layout.activity_admin_home);
                Toast.makeText(this, "Admin login successful (bypassed Firebase)", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });

        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    private void reload() {
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String email = user.getEmail();

            if (email == null) {
                Toast.makeText(this, "User email not available", Toast.LENGTH_SHORT).show();
                return;
            }

            if (1 == 1) {
                setContentView(R.layout.activity_organizer_home);
            } else if (email.contains("admin")) {
                setContentView(R.layout.activity_participant_home);
            } else {
                Toast.makeText(this, "Unrecognized user role", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
