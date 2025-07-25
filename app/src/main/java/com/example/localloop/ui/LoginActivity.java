package com.example.localloop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.database.Database;
import com.example.localloop.usertype.AdminUser;
import com.example.localloop.usertype.User;
import com.example.localloop.database.UserOperation;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private boolean isSignupScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If a user is already signed in, go straight to their home
        FirebaseUser existing = FirebaseAuth.getInstance().getCurrentUser();
        if (existing != null) {
            routeSignIn(existing);
            return;
        }

        loginLayout(); // no signed‐in user, show login form
    }


    //TODO NEED TO REIMPLEMENT LOGIN LOGIC USING DATABASE GETUSER()
    private void loginLayout() {
        isSignupScreen = false;


        Log.d("Login", "THIS IS Login PAGE");

        setContentView(R.layout.login_activity);

        //SIGNUP CLICKED
        Button btn_login_signup = findViewById(R.id.btn_login_signup);
        btn_login_signup.setOnClickListener(v -> signupLayout());

        //LOGIN CLICKED
        Button btn_login_login = findViewById(R.id.btn_login_login);
        btn_login_login.setOnClickListener(v -> {

            EditText emailField = findViewById(R.id.text_login_email);
            EditText passwordField = findViewById(R.id.text_login_password);

            String email = emailField.getText().toString().trim().toLowerCase();
            String password = passwordField.getText().toString().trim();


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or Password can not be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.equals("admin")) {
                email = "admin@domain.com"; // Admin email is hardcoded
            }
            else{
                // Check if email is valid
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Attempt to login via UserOperation
            UserOperation.signInUserAuthEmailAndPassword(
                email,
                password,
                firebaseUser -> {
                    String userEmail = firebaseUser.getEmail();
                    Toast.makeText(this, "Logging in as: " + userEmail, Toast.LENGTH_SHORT).show();

                    // Route to the appropriate home screen based on user role
                    routeSignIn(firebaseUser);
                },
                exception -> {
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            );
        });
    }

    private void signupLayout() {
        isSignupScreen = true;

        Log.d("LAYOUT", "THIS IS SIGNUP PAGE");

        setContentView(R.layout.signup_activity);



        Button btn_signup_back = findViewById(R.id.btn_signup_back);
        btn_signup_back.setOnClickListener(v->{loginLayout();});

        Button btn_signup_signup = findViewById(R.id.btn_signup_signup);
        btn_signup_signup.setOnClickListener(v -> {
            //GET INFO
            EditText emailField = findViewById(R.id.text_signup_email);
            EditText firstnameField = findViewById(R.id.text_signup_firstname);
            EditText lastnameField = findViewById(R.id.text_signup_lastname);
            EditText usernameField = findViewById(R.id.text_signup_username);
            EditText passwordField = findViewById(R.id.text_signup_password);
            EditText confirmField = findViewById(R.id.text_signup_confirmPassword);
            RadioButton organizer = findViewById(R.id.radio_signup_organizer);
            RadioButton participant = findViewById(R.id.radio_signup_participant);

            String email = emailField.getText().toString().trim();
            String firstname = firstnameField.getText().toString().trim();
            String lastname = lastnameField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmField.getText().toString().trim();

            //FIELD AND DB CHECK
            if (email.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fields can not be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
                return;
            }

    /*        // Check if email already exists - to be complete later
            if(Database.get("users") contain email) {
                Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show();
            }*/


            //CREATE USER AND GOTO ROLE SPECIFIC HOME
            User user;

            if (organizer.isChecked()) {
                user = new OrganizerUser(email, username, "organizer", firstname, lastname, null);
                UserOperation.signUpUserAuth(
                    user,
                    password,
                    firebaseUser -> {
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
                        routeSignIn(firebaseUser);
                    },
                    exception -> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show()
                );
            } else if (participant.isChecked()) {
                user = new ParticipantUser(email, username, "participant", firstname, lastname, null);
                UserOperation.signUpUserAuth(
                    user,
                    password,
                    firebaseUser -> {
                        Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show();
                        routeSignIn(firebaseUser);
                    },
                    exception -> Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void routeSignIn(FirebaseUser firebaseUser) {
        Database.get("users", firebaseUser.getUid(), userData -> {
            if (userData == null) {
                Log.e("LOGIN", "User data not found for email: " + firebaseUser.getEmail());
                Toast.makeText(this, "Your account data has been lost. You must sign up again with a different email.", Toast.LENGTH_SHORT).show();
                UserOperation.signOutUserAuth(); // Sign out the user
                return;
            }

            String userRole = (String) userData.get("user_role");

            if ("organizer".equals(userRole)){
                UserOperation.currentUser = new OrganizerUser(
                        Objects.requireNonNull(userData.get("user_email")).toString(),
                        Objects.requireNonNull(userData.get("user_name")).toString(),
                        Objects.requireNonNull(userData.get("user_role")).toString(),
                        Objects.requireNonNull(userData.get("first_name")).toString(),
                        Objects.requireNonNull(userData.get("last_name")).toString(),
                        Objects.requireNonNull(firebaseUser.getUid())
                );
            }
            else if ("participant".equals(userRole)){
                UserOperation.currentUser = new ParticipantUser(
                        Objects.requireNonNull(userData.get("user_email")).toString(),
                        Objects.requireNonNull(userData.get("user_name")).toString(),
                        Objects.requireNonNull(userData.get("user_role")).toString(),
                        Objects.requireNonNull(userData.get("first_name")).toString(),
                        Objects.requireNonNull(userData.get("last_name")).toString(),
                        Objects.requireNonNull(firebaseUser.getUid())
                );
            } else if ("admin".equals(userRole)){
                UserOperation.currentUser = new AdminUser(
                        Objects.requireNonNull(userData.get("user_email")).toString(),
                        Objects.requireNonNull(userData.get("user_name")).toString(),
                        Objects.requireNonNull(userData.get("user_role")).toString(),
                        Objects.requireNonNull(userData.get("first_name")).toString(),
                        Objects.requireNonNull(userData.get("last_name")).toString(),
                        Objects.requireNonNull(firebaseUser.getUid())
                );
            }

            Log.d("LOGIN", "Logged in as: " + UserOperation.currentUser.getUserName() + " (" + UserOperation.currentUser.user_role + ")");
            Toast.makeText(this, ("Logged in as: " + UserOperation.currentUser.getUserName() + " (" + UserOperation.currentUser.user_role + ")"), Toast.LENGTH_SHORT).show();


            if ("admin".equals(UserOperation.currentUser.user_role)) {
                Log.d("LOGIN", "Go to admin activity");

                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            }
            //REDIRECT
            else if ("organizer".equals(UserOperation.currentUser.user_role)) {
                Log.d("LOGIN", "Go to organizer activity");

                Intent intent = new Intent(this, OrganizerActivity.class);
                startActivity(intent);
            }
            else {
                Log.d("LOGIN", "Go to participant activity");

                Intent intent = new Intent(this, ParticipantActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (isSignupScreen) {
            loginLayout();
        }
    }
}