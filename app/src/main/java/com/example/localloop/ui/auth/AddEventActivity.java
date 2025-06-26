package com.example.localloop.ui.auth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.localloop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    // UI components
    private EditText eventNameEditText, eventDescriptionEditText, eventFeeEditText;
    private TextView dateTextView, timeTextView;
    private Spinner categorySpinner;
    private Button saveButton;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Lists to store category names and their corresponding IDs
    private ArrayList<String> categoryNames = new ArrayList<>();
    private ArrayList<String> categoryIds = new ArrayList<>();
    private String selectedCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get references to all UI elements
        eventNameEditText = findViewById(R.id.editTextEventName);
        eventDescriptionEditText = findViewById(R.id.editTextEventDescription);
        eventFeeEditText = findViewById(R.id.editTextEventFee);
        dateTextView = findViewById(R.id.textViewEventDate);
        timeTextView = findViewById(R.id.textViewEventTime);
        categorySpinner = findViewById(R.id.spinnerCategory);
        saveButton = findViewById(R.id.buttonSaveEvent);

        // Load categories from Firestore into the Spinner
        loadCategories();

        // Set up date picker dialog
        dateTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                // Format selected date and set it to the TextView
                String date = day + "/" + (month + 1) + "/" + year;
                dateTextView.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Set up time picker dialog
        timeTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                // Format selected time and set it to the TextView
                String time = String.format("%02d:%02d", hour, minute);
                timeTextView.setText(time);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // When the Save button is clicked, attempt to validate and save the event
        saveButton.setOnClickListener(v -> saveEvent());
    }

    // Loads all categories from Firestore and populates the Spinner
    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String id = doc.getId();
                String name = doc.getString("name");
                if (id != null && name != null) {
                    categoryIds.add(id);
                    categoryNames.add(name);
                }
            }
            // Populate the Spinner with the loaded category names
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
        );
    }

    // Validates user input and saves the event to Firestore
    private void saveEvent() {
        String name = eventNameEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String feeStr = eventFeeEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();
        String time = timeTextView.getText().toString().trim();

        // Input validation
        if (name.isEmpty() || description.isEmpty() || date.equals("Select Date") || time.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse fee, default to 0 if empty
        double fee = 0;
        try {
            fee = feeStr.isEmpty() ? 0 : Double.parseDouble(feeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected category ID from Spinner
        int selectedIndex = categorySpinner.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= categoryIds.size()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedCategoryId = categoryIds.get(selectedIndex);

        // Get the current organizer's user ID
        String organizerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        // Prepare the event data to upload to Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("organizerId", organizerId);
        eventData.put("name", name);
        eventData.put("description", description);
        eventData.put("categoryId", selectedCategoryId);
        eventData.put("fee", fee);
        eventData.put("date", date);
        eventData.put("time", time);

        // Save the event to Firestore under the "events" collection
        db.collection("events").add(eventData)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
