package com.example.localloop.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.databse.Category;
import com.example.localloop.databse.Database;
import com.example.localloop.usertype.User;

import java.util.List;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminHomeLayout(); // goto login page
    }


    private void adminHomeLayout() {
        Log.d("LAYOUT", "THIS IS admin_home_activity PAGE");
        setContentView(R.layout.admin_home_activity);

        //MANAGE EVENTS CLICKED
        Button btn_admin_manageCategory = findViewById(R.id.btn_admin_manageCategory);
        btn_admin_manageCategory.setOnClickListener(v -> manageCategoryLayout());

        //MANAGE USERS CLICKED
        Button btn_admin_manageUsers = findViewById(R.id.btn_admin_manageUsers);
        btn_admin_manageUsers.setOnClickListener(v -> manageUsersLayout());
    }

    private void manageCategoryLayout() {
        Log.d("LAYOUT", "THIS IS admin_manage_category_activity PAGE");
        setContentView(R.layout.admin_manage_category_activity);

        List<Category> categoryList = Database.getCategory();


        /*
        TODO

        Display Category list with Add, Edit, Delete
         */
    }

    private void manageUsersLayout() {

        Log.d("LAYOUT", "THIS IS admin_manage_users_activity PAGE");
        setContentView(R.layout.admin_manage_users_activity);

        List<User> userList = Database.getUser();

        /*
        TODO

        Display User List with delete and disable
         */
    }
}