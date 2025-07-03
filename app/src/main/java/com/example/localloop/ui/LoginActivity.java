package com.example.localloop.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;



public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginLayout(); // goto login page
    }


    private void loginLayout() {
        Log.d("LAYOUT", "THIS IS LAYOUT PAGE");

        setContentView(R.layout.login_activity);

        Button btn_login_signup = findViewById(R.id.btn_login_signup);
        btn_login_signup.setOnClickListener(v -> signupLayout());


    }

    private void signupLayout() {
        Log.d("LAYOUT", "THIS IS SIGNUP PAGE");

        setContentView(R.layout.signup_activity);



        Button btn_login_signup = findViewById(R.id.btn_signup_back);
        btn_login_signup.setOnClickListener(v -> loginLayout());

        Button btn_signup_signup = findViewById(R.id.btn_signup_signup);
        btn_login_signup.setOnClickListener(v -> loginLayout());


        //GET INFO

        //CHECK DB AND CREATE NEW USER

        //REDIRECT TO USER ROLE SPECIFIC PAGE
    }
}
