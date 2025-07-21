package com.example.localloop; 

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.ui.AdminActivity;
import com.example.localloop.ui.LoginActivity;
import com.example.localloop.ui.OrganizerActivity;
import com.example.localloop.ui.ParticipantActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirects
        Intent login = new Intent(this, LoginActivity.class);
        Intent admin = new Intent(this, AdminActivity.class);
        Intent organizer = new Intent(this, OrganizerActivity.class);
        Intent participant = new Intent(this, ParticipantActivity.class);



        startActivity(login);
    }
}

