package com.example.localloop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        RecyclerView rv = findViewById(R.id.recycler_category_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Database.getCategory(categories -> {
            runOnUiThread(() -> {
                CategoryAdapter adapter = new CategoryAdapter(categories);
                rv.setAdapter(adapter);
            });
        });
    }

    //USE THIS IF CREATING NEW
    private void addCategoryLayout() {
        Log.d("LAYOUT", "admin_add_category_activity");
        setContentView(R.layout.admin_add_category_activity);

        Button btnSubmit = findViewById(R.id.btn_add_category_submit);
        EditText categoryNameField = findViewById(R.id.text_category_name);
        EditText categoryDescriptionField = findViewById(R.id.text_category_description);

        btnSubmit.setOnClickListener(v -> {
            String categoryName = categoryNameField.getText().toString().trim();
            String categoryDescription = categoryDescriptionField.getText().toString().trim();

            Category category = new Category(categoryName, categoryDescription);
            CategoryOperation.addCategory(category);
            Toast.makeText(this, "Category Created", Toast.LENGTH_SHORT).show();

            manageCategoryLayout();
        });
    }

    //USE THIS TO EDIT EXIST CATEGORY INFO
    private void addCategoryLayout(Category category) {
        Log.d("LAYOUT", "admin_add_category_activity");
        setContentView(R.layout.admin_add_category_activity);

        EditText categoryNameField = findViewById(R.id.text_category_name);
        EditText categoryDescriptionField = findViewById(R.id.text_category_description);
        Button btnSubmit = findViewById(R.id.btn_add_category_submit);

        if (category != null) {
            categoryNameField.setText(category.getCategory_name());
            categoryDescriptionField.setText(category.category_description);
        }

        btnSubmit.setOnClickListener(v -> {
            String categoryName = categoryNameField.getText().toString().trim();
            String categoryDescription = categoryDescriptionField.getText().toString().trim();

            if (category != null) {
                CategoryOperation.deleteCategory(category);
            }

            Category newCategory = new Category(categoryName, categoryDescription);
            CategoryOperation.addCategory(newCategory);
            Toast.makeText(this, "Category Updated", Toast.LENGTH_SHORT).show();

            manageCategoryLayout();
        });
    }


    private void manageUsersLayout() {
        Log.d("LAYOUT", "THIS IS admin_manage_users_activity PAGE");
        setContentView(R.layout.admin_manage_users_activity);

        RecyclerView rv = findViewById(R.id.recycler_user_list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Database.getUserSync(users -> {
            runOnUiThread(() -> {
                UserAdapter adapter = AdminActivity.this.new UserAdapter(users);
                rv.setAdapter(adapter);
            });
        });
    }





    //For category items
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

        private final List<Category> items;

        CategoryAdapter(List<Category> items) {
            this.items = items;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView name, desc;
            Button btnEdit, btnDelete;

            VH(View itemView) {
                super(itemView);
                name      = itemView.findViewById(R.id.text_category_item_name);
                desc      = itemView.findViewById(R.id.text_category_item_description);
                btnEdit   = itemView.findViewById(R.id.btn_edit_category);
                btnDelete = itemView.findViewById(R.id.btn_delete_category);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int pos) {
            Category c = items.get(pos);
            holder.name.setText(c.getCategory_name());
            holder.desc.setText(c.category_description);

            holder.btnEdit.setOnClickListener(v -> {
                addCategoryLayout(c);
            });

            holder.btnDelete.setOnClickListener(v -> {
                CategoryOperation.deleteCategory(c);
                items.remove(pos);
                notifyItemRemoved(pos);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

        private final List<User> items;

        UserAdapter(List<User> items) {
            this.items = items;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView name, email, role, firstName, lastName, status;
            Button btnDisable, btnEnable;

            VH(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.text_user_item_username);
                email = itemView.findViewById(R.id.text_user_item_email);
                role = itemView.findViewById(R.id.text_user_item_role);
                firstName = itemView.findViewById(R.id.text_user_item_first_name);
                lastName = itemView.findViewById(R.id.text_user_item_last_name);
                status = itemView.findViewById(R.id.text_user_item_status);
                btnDisable = itemView.findViewById(R.id.btn_user_item_disable);
                btnEnable = itemView.findViewById(R.id.btn_user_item_enable);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int pos) {
            User user = items.get(pos);

            holder.name.setText(user.user_name);
            holder.email.setText(user.user_email);
            holder.role.setText(user.user_role);
            holder.firstName.setText(user.first_name);
            holder.lastName.setText(user.last_name);
            holder.status.setText("Status: " + (user.isDisabled() ? "Disabled" : "Active"));

            if (user.isDisabled()) {
                holder.btnDisable.setVisibility(View.GONE);
                holder.btnEnable.setVisibility(View.VISIBLE);
            } else {
                holder.btnDisable.setVisibility(View.VISIBLE);
                holder.btnEnable.setVisibility(View.GONE);
            }

            holder.btnDisable.setOnClickListener(v -> {
                user.setDisable(true);
                UserOperation.addUserAccount(user);

                holder.status.setText("Status: Disabled");
                holder.btnDisable.setVisibility(View.GONE);
                holder.btnEnable.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(), "User disabled", Toast.LENGTH_SHORT).show();
            });

            holder.btnEnable.setOnClickListener(v -> {
                user.setDisable(false);
                UserOperation.addUserAccount(user);

                holder.status.setText("Status: Active");
                holder.btnDisable.setVisibility(View.VISIBLE);
                holder.btnEnable.setVisibility(View.GONE);
                Toast.makeText(v.getContext(), "User enabled", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


}
