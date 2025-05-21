package com.example.localloop.ui.auth;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;


public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    private EditText emailField, usernameField, passwordField;
    private Button submitButton;

    private String email, username, password;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);

        emailField = findViewById(R.id.inputEmail);
        usernameField = findViewById(R.id.inputUsername);
        passwordField = findViewById(R.id.inputPassword);
        submitButton = findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(v -> {
            email = emailField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            password = passwordField.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    private void reload() {
        // TODO: Implement reload logic
    }

    private void updateUI(FirebaseUser user) {
        // TODO: Implement update UI logic
    }
}
