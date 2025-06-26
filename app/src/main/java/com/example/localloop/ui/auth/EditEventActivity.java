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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {

    private EditText eventNameEditText, eventDescriptionEditText, eventFeeEditText;
    private TextView dateTextView, timeTextView;
    private Spinner categorySpinner;
    private Button saveButton;

    private FirebaseFirestore db;
    private String eventId;

    private ArrayList<String> categoryNames = new ArrayList<>();
    private ArrayList<String> categoryIds = new ArrayList<>();
    private String selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event); // Reuse same layout

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get eventId passed via Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI components
        eventNameEditText = findViewById(R.id.editTextEventName);
        eventDescriptionEditText = findViewById(R.id.editTextEventDescription);
        eventFeeEditText = findViewById(R.id.editTextEventFee);
        dateTextView = findViewById(R.id.textViewEventDate);
        timeTextView = findViewById(R.id.textViewEventTime);
        categorySpinner = findViewById(R.id.spinnerCategory);
        saveButton = findViewById(R.id.buttonSaveEvent);
        saveButton.setText("Update Event");

        // Load categories first, then event details
        loadCategories();

        // Set up date picker
        dateTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = day + "/" + (month + 1) + "/" + year;
                dateTextView.setText(date);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Set up time picker
        timeTextView.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                String time = String.format("%02d:%02d", hour, minute);
                timeTextView.setText(time);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Handle update button click
        saveButton.setOnClickListener(v -> updateEvent());
    }

    // Load all categories into Spinner
    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(querySnapshot -> {
            categoryNames.clear();
            categoryIds.clear();

            for (DocumentSnapshot doc : querySnapshot) {
                String id = doc.getId();
                String name = doc.getString("name");

                if (id != null && name != null) {
                    categoryIds.add(id);
                    categoryNames.add(name);
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);

            // After categories load, load event data
            loadEventData();
        });
    }

    // Load event data from Firestore and pre-fill fields
    private void loadEventData() {
        db.collection("events").document(eventId).get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            eventNameEditText.setText(doc.getString("name"));
            eventDescriptionEditText.setText(doc.getString("description"));
            eventFeeEditText.setText(String.valueOf(doc.getDouble("fee")));
            dateTextView.setText(doc.getString("date"));
            timeTextView.setText(doc.getString("time"));

            String catId = doc.getString("categoryId");
            if (catId != null) {
                int index = categoryIds.indexOf(catId);
                if (index != -1) categorySpinner.setSelection(index);
            }
        });
    }

    // Validate and update event in Firestore
    private void updateEvent() {
        String name = eventNameEditText.getText().toString().trim();
        String description = eventDescriptionEditText.getText().toString().trim();
        String feeStr = eventFeeEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();
        String time = timeTextView.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || date.equals("Select Date") || time.equals("Select Time")) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double fee = 0;
        try {
            fee = feeStr.isEmpty() ? 0 : Double.parseDouble(feeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Fee must be a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex = categorySpinner.getSelectedItemPosition();
        if (selectedIndex < 0 || selectedIndex >= categoryIds.size()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedCategoryId = categoryIds.get(selectedIndex);

        // Update Firestore document
        Map<String, Object> updatedEvent = new HashMap<>();
        updatedEvent.put("name", name);
        updatedEvent.put("description", description);
        updatedEvent.put("fee", fee);
        updatedEvent.put("date", date);
        updatedEvent.put("time", time);
        updatedEvent.put("categoryId", selectedCategoryId);

        db.collection("events").document(eventId).update(updatedEvent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close screen
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
