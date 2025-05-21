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


//Firebase guide https://firebase.google.com/docs/auth/android/password-auth#java_1
public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    private EditText emailField, usernameField, passwordField;

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
        Button submitButton = findViewById(R.id.buttonSubmit);

        submitButton.setOnClickListener(v -> {
            email = emailField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            password = passwordField.getText().toString().trim();

            //basic check
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            //firebase auth https://firebase.google.com/docs/auth/android/password-auth#java_1
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

    //firebase auth https://firebase.google.com/docs/auth/android/password-auth#java_1
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }


    private void reload() {
    }

    //IMPORTANT TODO! Differenciate user type then send them into their corespond layout
    private void updateUI(FirebaseUser user) {
        //SOMETHING LIKE below in loginActivity

//        if (user != null) {
//            String email = user.getEmail();
//
//            //Basic check
//            if (email == null) {
//                Toast.makeText(this, "User email not available", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            //Hard coded
//            if (1 == 1) {
//                setContentView(R.layout.activity_organizer_home);
//            } else if (email.contains("admin")) {
//                setContentView(R.layout.activity_participant_home);
//            } else {
//                Toast.makeText(this, "Unrecognized user role", Toast.LENGTH_SHORT).show();
//            }
//        }

        
    }
}
