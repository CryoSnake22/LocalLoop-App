package com.example.localloop.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.databse.Database;
import com.example.localloop.databse.User;
import com.example.localloop.databse.UserOperation;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginLayout(); // goto login page
    }


    private void loginLayout() {
        Log.d("LAYOUT", "THIS IS LAYOUT PAGE");

        setContentView(R.layout.login_activity);

        //Direct to Sign up
        Button btn_login_signup = findViewById(R.id.btn_login_signup);
        btn_login_signup.setOnClickListener(v -> signupLayout());

        //Login
        Button btn_login_login = findViewById(R.id.btn_login_login);
        btn_login_login.setOnClickListener(v -> signupLayout());

        String email = findViewById(R.id.text_login_email).toString().trim();
        String passord = findViewById(R.id.text_login_password).toString().trim();
    }

    private void signupLayout() {
        Log.d("LAYOUT", "THIS IS SIGNUP PAGE");

        setContentView(R.layout.signup_activity);



        Button btn_login_signup = findViewById(R.id.btn_signup_back);
        btn_login_signup.setOnClickListener(v -> loginLayout());

        Button btn_signup_signup = findViewById(R.id.btn_signup_signup);
        btn_login_signup.setOnClickListener(v -> loginLayout());


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
        if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
        }

        // Check if email already exists
        if(Database.get("user") contain email) {
            Toast.makeText(this, "This email is already registered!", Toast.LENGTH_SHORT).show();
        }


        //CREATE USER AND GOTO ROLE SPECIFIC HOME
        User user;

        if(organizer.isChecked()) {
            user = new OrganizerUser(email, username, password, "organizer", firstname, lastname);
            UserOperation.addUserAccount(user);

        }
        else if(participant.isChecked()){
            user = new ParticipantUser(email, username, password, "participant", firstname, lastname);
            UserOperation.addUserAccount(user);

        }


    }
}
