package com.example.localloop.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.localloop.R;
import com.example.localloop.data.model.User;
import com.example.localloop.utils.UserUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.data.model.Event;
import com.example.localloop.ui.auth.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerDashboard extends AppCompatActivity {

    private User user;
    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private List<Event> eventList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView textWelcome;
    private Map<String, String> categoryMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge screen layout
        EdgeToEdge.enable(this);

        // Set the layout for this activity
        setContentView(R.layout.activity_organizer_dashboard);

        // Handle system insets (like status bar and nav bar spacing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get reference to the welcome text
        textWelcome = findViewById(R.id.textWelcome);



        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        // Set up adapter and bind it to the RecyclerView
        eventAdapter = new EventAdapter(eventList, this);
        rvEvents.setAdapter(eventAdapter);




        // Receive UID passed from login or previous screen
        Intent intent = getIntent();
        String UID = intent.getStringExtra("UID");

        // Load user data using the UID and display a welcome message
        UserUtils.UIDtoUserAsync(UID, userAsync -> {
            if (userAsync != null) {
                this.user = userAsync;

                // Create a dynamic welcome message based on role
                String message = "Welcome " + user.getFirstName() + ", you are logged in as: " + user.getRole().toString().toLowerCase();
                textWelcome.setText(message);
            }
        });

        // Button to open the "Add Event" screen
        Button btnAddEvent = findViewById(R.id.btnAddEvent);

        // When the button is clicked, open AddEventActivity
        btnAddEvent.setOnClickListener(v -> {
            Intent i = new Intent(OrganizerDashboard.this, AddEventActivity.class);
            startActivity(i);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        fetchEventsForOrganizer(); // Always reload events when returning to this screen
    }

    private void fetchEventsForOrganizer() {
        // Clear the current list to prevent duplicates
        eventList.clear();

        // Step 1: Load all categories into categoryMap
        db.collection("categories").get().addOnSuccessListener(categorySnapshots -> {
            categoryMap.clear();

            for (DocumentSnapshot doc : categorySnapshots) {
                categoryMap.put(doc.getId(), doc.getString("name"));
            }

            String currentUID = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
            if (currentUID == null) {
                Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Query Firestore for events where organizerId == currentUID
            db.collection("events")
                    .whereEqualTo("organizerId", currentUID)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(this, "You have no events yet.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot doc : querySnapshot) {
                                Event event = doc.toObject(Event.class);
                                event.setId(doc.getId()); // Optional: store Firestore doc ID
                                eventList.add(event);
                            }
                            eventAdapter.setCategoryMap(categoryMap); // Pass category map to adapter
                            eventAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }




}
