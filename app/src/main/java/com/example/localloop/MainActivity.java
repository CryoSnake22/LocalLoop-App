package com.example.localloop; 

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.databse.Category;
import com.example.localloop.databse.CategoryOperation;
import com.example.localloop.databse.Database;
import com.example.localloop.databse.Event;
import com.example.localloop.databse.EventOperation;
import com.example.localloop.databse.User;
import com.example.localloop.databse.UserOperation;
import com.example.localloop.ui.AdminActivity;
import com.example.localloop.ui.LoginActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        OPTIONAL
        Auto login if user has already logged in, with cookie or something
         */


        List<User> userList = Database.getUser();


        Event e = new Event("event1", "event1", "event1", 100, "event1", "event1", "admin");
        Database.add("events", "12345", e.toMap());

/*        // Redirect to login
        Intent login = new Intent(this, LoginActivity.class);

        Intent admin = new Intent(this, AdminActivity.class);

        startActivity(admin);
        finish(); // Close MainActivity so user can't return to it

        */


    }
}

