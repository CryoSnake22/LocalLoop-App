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
import com.example.localloop.data.model.Role;
import com.example.localloop.data.model.User;
import com.example.localloop.utils.UserUtils;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


//Firebase guide https://firebase.google.com/docs/auth/android/password-auth#java_1
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";
    private EditText emailField, passwordField;

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);
        emailField = findViewById(R.id.inputEmail);
        passwordField = findViewById(R.id.inputPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button signupButton = findViewById(R.id.buttonSignup);


        //What happen after click login
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            //Basic check
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show();
                return;
            }

            //Hard code admin login, bypasses firebase auth below
            if (email.equals("admin") && password.equals("XPI76SZUqyCjVxgnUjm0")) {
                setContentView(R.layout.activity_admin_home); //hardcode, only direct to to admin layout that's all
                Toast.makeText(this, "Admin login successful (bypassed Firebase)", Toast.LENGTH_SHORT).show();
                return;
            }

            //Firebase auth, checks email for you, https://firebase.google.com/docs/auth/android/password-auth#java_1
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser!=null){
                                    String uid = firebaseUser.getUid();
                                    //LAMBDAAAAAAAAAAAAA
                                    UserUtils.UIDtoUserAsync(uid, user ->{
                                        if (user != null){
                                            updateUI(user); // TO CHANGE TO OUR OWN USER CLASSES
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this, "User not found in DB",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        });

        // Goes to RegistrationActivity, need to differentiate participant and organizer
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    //Firebase auth thing https://firebase.google.com/docs/auth/android/password-auth#java_1
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    //Can be filled, not necessary
    private void reload() {
    }

    //Called after successfully authed
    private void updateUI(User user) {
        if (user != null) {
            String email = user.getEmail();
            Role role = user.getRole();

            //Basic check
            if (email == null) {
                Toast.makeText(this, "User email not available", Toast.LENGTH_SHORT).show();
                return;
            }

            //Hard coded
            if (role == Role.ORGANIZER) {

                Toast.makeText(this, "GOT TO ORGANIZER PAGE", Toast.LENGTH_SHORT).show();
                setContentView(R.layout.activity_organizer_home);
            } else if (role == Role.PARTICIPANT) {
                setContentView(R.layout.activity_participant_home);
            } else {
                Toast.makeText(this, "Unrecognized user role", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
