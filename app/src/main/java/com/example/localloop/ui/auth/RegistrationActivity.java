package com.example.localloop.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreTestDataWriter";

    private EditText emailField, usernameField, passwordField;
    private Button submitButton;

    private String email, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailField = findViewById(R.id.inputEmail);
        usernameField = findViewById(R.id.inputUsername);
        passwordField = findViewById(R.id.inputPassword);
        submitButton = findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(v -> {
            email = emailField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            password = passwordField.getText().toString().trim();

            Toast.makeText(this, "Captured input", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Data captured: " + email + ", " + username);
        });
    }
}
