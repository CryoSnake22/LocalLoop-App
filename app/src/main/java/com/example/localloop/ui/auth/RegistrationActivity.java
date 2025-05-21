package com.example.localloop.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText, passwordEditText, firstNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // Your registration layout

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Get references to your UI elements
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        firstNameEditText = findViewById(R.id.editTextFirstName);
        // Initialize your role selection mechanism here

        Button registerButton = findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String role = getSelectedRole(); // Implement this method to get "organizer" or "participant"

        // Basic validation (you'll need more thorough validation as per deliverable requirements)
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || role == null) {
            // Show an error message to the user
            return;
        }

        // 1. Create user with Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Authentication success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            // 2. Save additional user data to Firestore
                            Map<String, Object> userFirestoreData = new HashMap<>();
                            userFirestoreData.put("firstName", firstName);
                            userFirestoreData.put("role", role);

                            db.collection("users").document(uid)
                                    .set(userFirestoreData)
                                    .addOnSuccessListener(aVoid -> {
                                        // Firestore write success!
                                        // Now you can navigate to the Welcome Screen
                                        // and pass the firstName and role
                                        navigateToWelcomeScreen(firstName, role);

                                    })
                                    .addOnFailureListener(e -> {
                                        // Firestore write failed - handle the error
                                        // You might want to delete the auth user created in step 1 here
                                        // or flag this account as incomplete
                                        // Log the error or show a message
                                    });

                        } else {
                            // User somehow null after successful creation - handle appropriately
                        }

                    } else {
                        // Authentication failed
                        // Handle errors, e.g., password too weak, email already in use
                        // Log the error: Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                        // Show an error message to the user based on task.getException()
                    }
                });
    }

    // You need to implement this based on your UI for role selection
    private String getSelectedRole() {
        // Example: Check radio buttons or spinner value
        // return "organizer" or "participant";
        return null; // Placeholder
    }

    // Method to navigate to your Welcome Screen Activity
    private void navigateToWelcomeScreen(String firstName, String role) {
        // Example:
        // Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
        // intent.putExtra("firstName", firstName);
        // intent.putExtra("role", role);
        // startActivity(intent);
        // finish(); // Close the registration activity
    }

}
