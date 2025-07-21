package com.example.localloop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.database.Category;
import com.example.localloop.database.Database;
import com.example.localloop.database.Event;
import com.example.localloop.database.EventOperation;
import com.example.localloop.database.Request;
import com.example.localloop.database.UserOperation;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.Map;

public class OrganizerActivity extends AppCompatActivity {
    private boolean isOrgHome = false;
    private boolean isRequestPage = false;
    private boolean isEventPage = false;
    private boolean isEditEventPage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        organizerHomeLayout();
    }
    private String currentScreen = "home"; // default
    private Spinner spinnerCategory;
    private List<String> categoryList = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;



    private void organizerHomeLayout() {
        isOrgHome = true;
        isRequestPage = false;
        isEventPage = false;
        isEditEventPage=false;
        Log.d("LAYOUT", "THIS IS organizer_home_activity PAGE");
        setContentView(R.layout.organizer_home_activity);

        Button btnManageEvents = findViewById(R.id.btn_organizer_manage_events);
        btnManageEvents.setOnClickListener(v -> manageEventsLayout());

        Button btnPendingRequests = findViewById(R.id.btn_organizer_pending_request);
        btnPendingRequests.setOnClickListener(v -> manageRequestLayout());
        TextView welcomeMessage = findViewById(R.id.text_organizer_welcome);
        welcomeMessage.setText("Welcome, "+UserOperation.currentUser.getFirstName()+" "+UserOperation.currentUser.getLastName());
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());
        currentScreen = "home";


    }

    private void logout() {
        UserOperation.signOutUserAuth(); // signs out and sets currentUser to null
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // closes the current activity
    }


    private void manageRequestLayout() {
        isOrgHome = false;
        isRequestPage = true;
        isEventPage = false;
        isEditEventPage=false;
        Log.d("LAYOUT", "THIS IS organizer_manage_request_activity PAGE");
        setContentView(R.layout.organizer_manage_request_activity);

        RecyclerView recyclerView = findViewById(R.id.recycler_organizer_manage_requrest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Database.getAllRequests(requests -> {
            runOnUiThread(() -> {
                List<Request> pending = new ArrayList<>();
                String currentOrganizerUid = UserOperation.currentUser.getUid();

                for (Request r : requests) {
                    // Only show current user and pending request
                    if (r.requestStatus == 0 && r.getEventOwnerUid().equals(currentOrganizerUid)) {
                        pending.add(r);
                    }
                }

                recyclerView.setAdapter(new RequestAdapter(pending));
            });
        });
        currentScreen = "viewRequests";

    }

    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.VH> {

        private final List<Request> requests;

        RequestAdapter(List<Request> requests) {
            this.requests = requests;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView username, email, firstName, lastName;
            Button btnAccept, btnDecline;

            VH(View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.text_event_request_username);
                email = itemView.findViewById(R.id.text_event_request_email);
                firstName = itemView.findViewById(R.id.text_event_request_firstname);
                lastName = itemView.findViewById(R.id.text_event_request_lastname);
                btnAccept = itemView.findViewById(R.id.btn_event_request_accept);
                btnDecline = itemView.findViewById(R.id.btn_event_request_declien);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_request, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Request r = requests.get(position);

            holder.username.setText("Username: " + r.attendee.user_name);
            holder.email.setText("Email: " + r.attendee.user_email);
            holder.firstName.setText("First Name: " + r.attendee.first_name);
            holder.lastName.setText("Last Name: " + r.attendee.last_name);

            String docId = r.getRequestId();

            holder.btnAccept.setOnClickListener(v -> {
                r.requestStatus = 1;
                Database.set("requests", docId, r.toMap());
                Toast.makeText(v.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();

                // Refresh the view
                manageRequestLayout();
            });

            holder.btnDecline.setOnClickListener(v -> {
                r.requestStatus = -1;
                Database.set("requests", docId, r.toMap());
                Toast.makeText(v.getContext(), "Request declined", Toast.LENGTH_SHORT).show();

                // Refresh the view
                manageRequestLayout();
            });

        }

        @Override
        public int getItemCount() {
            return requests.size();
        }
    }


    private void manageEventsLayout() {
        isOrgHome = false;
        isRequestPage = false;
        isEventPage = true;
        isEditEventPage=false;
        Log.d("LAYOUT", "THIS IS organizer_manage_event_activity PAGE");
        setContentView(R.layout.organizer_manage_event_activity);

        Button btnCreate = findViewById(R.id.btn_organizer_create_event);
        btnCreate.setOnClickListener(v -> createEventLayout());

        RecyclerView rv = findViewById(R.id.recycler_organizer_manage_event);
        rv.setLayoutManager(new LinearLayoutManager(this)); // THIS WAS MISSING!

        Database.getEvents(events -> runOnUiThread(() -> {
            rv.setAdapter(new EventAdapter(events));
        }));
        currentScreen = "manageEvents";

    }

    private void createEventLayout() {
        isOrgHome = false;
        isRequestPage = false;
        isEventPage = false;
        isEditEventPage = true;
        Log.d("LAYOUT", "THIS IS organizer_add_event_activity PAGE");
        setContentView(R.layout.organizer_add_event_activity);

        ImageView imageEvent = findViewById(R.id.image_event);
        imageEvent.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        Button btnSubmit = findViewById(R.id.btn_organizer_add_event_submit);
        EditText nameField = findViewById(R.id.text_organizer_add_event_name);
        EditText descField = findViewById(R.id.text_organizer_add_event_description);
        EditText feeField = findViewById(R.id.text_organizer_add_event_fee);
        EditText dateField = findViewById(R.id.text_organizer_add_event_date);
        EditText timeField = findViewById(R.id.text_organizer_add_event_time);
        Spinner categorySpinner = findViewById(R.id.spinner_event_category);

        // Set date picker
        dateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        dateField.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Set time picker
        timeField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        timeField.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Fetch categories and populate spinner
        List<String> categoryList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Database.getCategory(categories -> runOnUiThread(() -> {
            for (Category c : categories) {
                categoryList.add(c.getCategory_name());
            }
            adapter.notifyDataSetChanged();
        }));

        btnSubmit.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String desc = descField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String time = timeField.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Event name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            float fee;
            try {
                fee = Float.parseFloat(feeField.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid fee", Toast.LENGTH_SHORT).show();
                return;
            }

            if (categorySpinner.getSelectedItem() == null) {
                Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedCategory = categorySpinner.getSelectedItem().toString();
            Event event = new Event(name, desc, selectedCategory, fee, date, time, (OrganizerUser) UserOperation.currentUser);
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri, event.getUniqueId(), downloadUrl -> {
                    event.setImageUrl(downloadUrl);
                    EventOperation.addEvent(event);
                    Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
                    manageEventsLayout();
                });
            } else {
                EventOperation.addEvent(event);
                Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
                manageEventsLayout();
            }

            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
            manageEventsLayout();
        });

        currentScreen = "addEvent";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            ImageView imageEvent = findViewById(R.id.image_event);
            imageEvent.setImageURI(selectedImageUri); // show preview
        }
    }



    private void editEventLayout(Event event) {
        isOrgHome = false;
        isRequestPage = false;
        isEventPage = false;
        isEditEventPage = true;
        setContentView(R.layout.organizer_add_event_activity);

        EditText nameField = findViewById(R.id.text_organizer_add_event_name);
        EditText descField = findViewById(R.id.text_organizer_add_event_description);
        EditText feeField = findViewById(R.id.text_organizer_add_event_fee);
        EditText dateField = findViewById(R.id.text_organizer_add_event_date);
        EditText timeField = findViewById(R.id.text_organizer_add_event_time);
        Spinner categorySpinner = findViewById(R.id.spinner_event_category);
        Button btnSubmit = findViewById(R.id.btn_organizer_add_event_submit);

        nameField.setText(event.eventName);
        descField.setText(event.description);
        feeField.setText(String.valueOf(event.eventFee));
        dateField.setText(event.eventDate);
        timeField.setText(event.eventTime);

        dateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        dateField.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        timeField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        timeField.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Load categories into Spinner
        List<String> categoryList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Database.getCategory(categories -> runOnUiThread(() -> {
            for (Category c : categories) {
                categoryList.add(c.getCategory_name());
            }
            adapter.notifyDataSetChanged();

            // Set selected category after spinner is populated
            int index = categoryList.indexOf(event.associatedCategory);
            if (index != -1) categorySpinner.setSelection(index);
        }));

        btnSubmit.setOnClickListener(v -> {
            float fee;
            try {
                fee = Float.parseFloat(feeField.getText().toString().trim());
            } catch (Exception e) {
                Toast.makeText(this, "Invalid fee", Toast.LENGTH_SHORT).show();
                return;
            }

            String newCategory = categorySpinner.getSelectedItem().toString();

            Event update = new Event(
                    nameField.getText().toString().trim(),
                    descField.getText().toString().trim(),
                    newCategory,
                    fee,
                    dateField.getText().toString().trim(),
                    timeField.getText().toString().trim(),
                    (OrganizerUser) UserOperation.currentUser
            );

            EventOperation.updateEvent(event, update);
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            manageEventsLayout();
        });
    }
    private void uploadImageToFirebase(Uri imageUri, String eventId, OnSuccessListener<String> onSuccess) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("event_images/" + eventId + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            onSuccess.onSuccess(uri.toString());
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    Log.e("ImageUpload", "Failed", e);
                });
    }



    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

        private final List<Event> items;

        EventAdapter(List<Event> items) {
            this.items = items;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView name, description, fee, category, date, time;
            Button btnEdit, btnDelete, btnViewParticipants;

            VH(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.text_item_event_name);
                description = itemView.findViewById(R.id.text_item_event_description);
                fee = itemView.findViewById(R.id.text_item_event_fee);
                category = itemView.findViewById(R.id.text_item_event_category);
                date = itemView.findViewById(R.id.text_item_event_date);
                time = itemView.findViewById(R.id.text_item_event_time);
                btnEdit = itemView.findViewById(R.id.btn_item_event_edit);
                btnDelete = itemView.findViewById(R.id.btn_item_event_delete);
                btnViewParticipants = itemView.findViewById(R.id.btn_item_event_view_participants);

            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int pos) {
            Event e = items.get(pos);
            holder.name.setText(e.eventName);
            holder.description.setText(e.description);
            holder.fee.setText("Fee: $" + e.eventFee);
            holder.category.setText("Category: " + e.associatedCategory);
            holder.date.setText("Date: " + e.eventDate);
            holder.time.setText("Time: " + e.eventTime);

            holder.btnEdit.setOnClickListener(v -> editEventLayout(e));

            holder.btnDelete.setOnClickListener(v -> {
                EventOperation.deleteEvent(e);
                items.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
            });

            holder.btnViewParticipants.setOnClickListener(v -> {
                e.getAttendees(attendees -> {
                    List<String> acceptedUsers = new ArrayList<>();

                    for (ParticipantUser attendee : attendees) {
                        if (attendee.getFirstName() != null && attendee.getLastName() != null) {
                            acceptedUsers.add(attendee.getFirstName() + " " + attendee.getLastName());
                        }
                    }

                    if (acceptedUsers.isEmpty()) {
                        acceptedUsers.add("No accepted participants yet.");
                    }

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Accepted Participants")
                            .setItems(acceptedUsers.toArray(new String[0]), null)
                            .setPositiveButton("OK", null)
                            .show();
                });
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
    @SuppressLint("MissingSuperCall")

    @Override
    public void onBackPressed() {
        switch (currentScreen) {
            case "manageEvents":
            case "addEvent":
            case "editEvent":
            case "viewRequests":  // 👈 add this line or the correct tag
                organizerHomeLayout(); // go back to home layout
                break;
            case "home":
            default:
                super.onBackPressed(); // exit activity
                break;
        }
    }




}
