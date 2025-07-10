package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.example.localloop.databse.Category;
import com.example.localloop.databse.CategoryOperation;
import com.example.localloop.databse.Database;
import com.example.localloop.databse.UserOperation;
import com.example.localloop.usertype.ParticipantUser;
import com.example.localloop.usertype.User;

import java.util.List;
import java.util.Objects;

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

        Button btn_add_category = findViewById(R.id.btn_add_category);
        btn_add_category.setOnClickListener(v -> addCategoryLayout());

        /*
        TODO

        Display Category list with Add, Edit, Delete
         */
    }

    private void addCategoryLayout() {
        Log.d("LAYOUT", "admin_add_category_activity");
        setContentView(R.layout.admin_add_category_activity);


        Button btn_login_login = findViewById(R.id.btn_add_category_submit);

        EditText categoryNameField = findViewById(R.id.text_category_name);
        EditText categoryDescriptionField = findViewById(R.id.text_category_description);

        String categoryName = categoryNameField.getText().toString().trim();
        String categoryDescription = categoryDescriptionField.getText().toString().trim();

        Category category = new Category(categoryName, categoryDescription);
        CategoryOperation.addCategory(category);
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