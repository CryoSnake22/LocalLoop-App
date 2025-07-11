package com.example.localloop; 

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.databse.Category;
import com.example.localloop.databse.CategoryOperation;
import com.example.localloop.databse.UserOperation;
import com.example.localloop.ui.AdminActivity;
import com.example.localloop.ui.LoginActivity;
import com.example.localloop.ui.OrganizerActivity;
import com.example.localloop.ui.ParticipantActivity;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        OPTIONAL
        Auto login if user has already logged in, with cookie or something
         */


        // Redirects
        Intent login = new Intent(this, LoginActivity.class);
        Intent admin = new Intent(this, AdminActivity.class);
        Intent organizer = new Intent(this, OrganizerActivity.class);
        Intent participant = new Intent(this, ParticipantActivity.class);


        CategoryOperation.addCategory(new Category("Category 1", ""));
        CategoryOperation.addCategory(new Category("Category 2", ""));
        CategoryOperation.addCategory(new Category("Category 3", ""));

        OrganizerUser org = new OrganizerUser("org@gmail.com", "user1", "12345678", "organizer", "org1", "anizer1");

        ParticipantUser par = new ParticipantUser("par@gmail.com", "user2", "12345678", "participant", "par", "ticipant");

        UserOperation.currentUser = org;

        startActivity(organizer);


        finish();


    }
}

