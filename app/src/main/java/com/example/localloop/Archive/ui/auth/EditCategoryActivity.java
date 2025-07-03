package com.example.localloop.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.localloop.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCategoryActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editDescription;
    private Button btnSave;

    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        initViews();
        loadCategoryDataFromIntent();
        setupSaveButton();
    }

    /**
     * Initializes UI components
     */
    private void initViews() {
        editName = findViewById(R.id.editTextCategoryName);
        editDescription = findViewById(R.id.editTextCategoryDescription);
        btnSave = findViewById(R.id.btnSaveChanges);
    }

    /**
     * Retrieves category data passed via Intent and populates the fields
     */
    private void loadCategoryDataFromIntent() {
        categoryId = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");

        if (categoryId == null || categoryId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid Category ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editName.setText(name);
        editDescription.setText(description);
    }


    /**
     * Configures the save button to update the Firestore document
     */
    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String updatedName = editName.getText().toString().trim();
            String updatedDesc = editDescription.getText().toString().trim();

            if (updatedName.isEmpty()) {
                editName.setError("Category name is required");
                return;
            }

            if (updatedDesc.isEmpty()) {
                editDescription.setError("Category description is required");
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("categories")
                    .document(categoryId)
                    .update("name", updatedName, "description", updatedDesc)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
