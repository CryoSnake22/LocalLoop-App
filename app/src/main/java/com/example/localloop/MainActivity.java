package com.example.localloop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.databse.Category;
import com.example.localloop.databse.CategoryOperation;
import com.example.localloop.databse.Event;
import com.example.localloop.databse.EventOperation;
import com.example.localloop.databse.User;
import com.example.localloop.databse.UserOperation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserOperation.addUserAccount(new User("email", "username", "password", "role", "firstname", "lastname"));
        CategoryOperation.addCategory(new Category("sport", "sport category description"));
        CategoryOperation.addCategory(new Category("music", "music category description"));
        CategoryOperation.addCategory(new Category("art", "art category description"));
        CategoryOperation.addCategory(new Category("music", "music2 category description"));
        EventOperation.addEvent(new Event("even1", "description of event 1", "sport, music, art", 100, "7/6/2024", "0900"));


    }
}

