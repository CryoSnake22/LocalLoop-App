package com.example.localloop.ui.auth;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import com.example.localloop.R;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText categoryNameEditText;
    private EditText categoryDescriptionEditText;
    private Button addCategoryButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Reference UI components
        categoryNameEditText = findViewById(R.id.editTextCategoryName);
        categoryDescriptionEditText = findViewById(R.id.editTextCategoryDescription);
        addCategoryButton = findViewById(R.id.btnNewCategory);

        // Set button click listener
        addCategoryButton.setOnClickListener(v -> {
            String name = categoryNameEditText.getText().toString().trim();
            String description = categoryDescriptionEditText.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> category = new HashMap<>();
            category.put("name", name);
            category.put("description", description);

            db.collection("categories")
                    .add(category)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
