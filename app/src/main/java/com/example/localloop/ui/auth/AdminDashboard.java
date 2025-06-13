package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.data.model.Category;
import com.example.localloop.data.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends AppCompatActivity {

    // Firebase
    private FirebaseFirestore db;

    // UI - Headers and Toggle Arrows
    private LinearLayout userLayout, eventLayout, categoryLayout;
    private ImageView arrowUser, arrowEvent, arrowCategory;
    private TextView textWelcome;

    // RecyclerViews & Adapters
    private RecyclerView rvUser, rvEvent, rvCategory;
    private UserAdapter userAdapter;
    private CategoryAdapter categoryAdapter;

    // Data Lists
    private List<User> userList;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        // Apply padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        initViews();
        setupUserRecycler();
        setupCategoryRecycler();
        fetchUsers();
        setupHeaderListeners();

        // Display welcome message
        String UID = getIntent().getStringExtra("UID");
        String message = "Welcome Admin, you are logged in as: admin";
        textWelcome.setText(message);

        // Add new category button
        Button btnNewCategory = findViewById(R.id.btnNewCategory);
        btnNewCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AddCategoryActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Initialize UI components from layout
     */
    private void initViews() {
        textWelcome = findViewById(R.id.textWelcome);

        userLayout = findViewById(R.id.headerUsers);
        eventLayout = findViewById(R.id.headerEvents);
        categoryLayout = findViewById(R.id.headerCategories);

        arrowUser = findViewById(R.id.arrowUsers);
        arrowEvent = findViewById(R.id.arrowEvents);
        arrowCategory = findViewById(R.id.arrowCategories);

        rvUser = findViewById(R.id.rvUsers);
        rvEvent = findViewById(R.id.rvEvents);
        rvCategory = findViewById(R.id.rvCategories);
    }

    /**
     * Setup the user RecyclerView and adapter
     */
    private void setupUserRecycler() {
        rvUser.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        rvUser.setAdapter(userAdapter);
    }

    /**
     * Setup the category RecyclerView and adapter
     */
    private void setupCategoryRecycler() {
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, this);
        rvCategory.setAdapter(categoryAdapter);
    }

    /**
     * Setup expandable/collapsible section headers
     */
    private void setupHeaderListeners() {
        userLayout.setOnClickListener(v -> toggleSection(rvUser, arrowUser));
        eventLayout.setOnClickListener(v -> toggleSection(rvEvent, arrowEvent));
        categoryLayout.setOnClickListener(v -> {
            toggleSection(rvCategory, arrowCategory);
            if (rvCategory.getVisibility() == View.VISIBLE) {
                fetchCategories();
            }
        });
    }

    /**
     * Toggle section visibility with arrow rotation
     */
    private void toggleSection(RecyclerView recyclerView, ImageView arrow) {
        boolean isVisible = recyclerView.getVisibility() == View.VISIBLE;
        recyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.setRotation(isVisible ? -90 : 0);
    }

    /**
     * Fetch users from Firestore and update the list in real time
     */
    private void fetchUsers() {
        db.collection("user_db").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;

            userList.clear();
            for (DocumentSnapshot snapshot : value.getDocuments()) {
                User user = snapshot.toObject(User.class);
                if (user != null) {
                    userList.add(user);
                } else {
                    Log.w("AdminDashboard", "NULL USER DETECTED");
                }
            }
            userAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Fetch categories from Firestore and update the list in real time
     */
    private void fetchCategories() {
        db.collection("categories").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("AdminDashboard", "Firestore error", error);
                return;
            }

            categoryList.clear();

            if (value != null && !value.isEmpty()) {
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    Category category = snapshot.toObject(Category.class);
                    if (category != null) {
                        category.setId(snapshot.getId());
                        categoryList.add(category);
                    }
                }
            }

            categoryAdapter.notifyDataSetChanged();
        });
    }
}
