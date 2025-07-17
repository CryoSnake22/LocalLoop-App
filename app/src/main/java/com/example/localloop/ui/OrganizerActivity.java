package com.example.localloop.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class OrganizerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        organizerHomeLayout();
    }



    private void organizerHomeLayout() {
        Log.d("LAYOUT", "THIS IS organizer_home_activity PAGE");
        setContentView(R.layout.organizer_home_activity);

        Button btnManageEvents = findViewById(R.id.btn_organizer_manage_events);
        btnManageEvents.setOnClickListener(v -> manageEventsLayout());

        Button btnPendingRequests = findViewById(R.id.btn_organizer_pending_request);
        btnPendingRequests.setOnClickListener(v -> manageRequestLayout());
    }

    private void manageRequestLayout() {
        Log.d("LAYOUT", "THIS IS organizer_manage_request_activity PAGE");
        setContentView(R.layout.organizer_manage_request_activity);

        RecyclerView recyclerView = findViewById(R.id.recycler_organizer_manage_requrest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Database.getAllRequests(requests -> {
            runOnUiThread(() -> {
                List<Request> pending = new ArrayList<>();
                String currentOrganizerEmail = UserOperation.currentUser.getEmail();

                for (Request r : requests) {
                    // Only show current user and pending request
                    if (r.requestStatus == 0 && r.getEventOwnerEmail().equals(currentOrganizerEmail)) {
                        pending.add(r);
                    }
                }

                recyclerView.setAdapter(new RequestAdapter(pending));
            });
        });


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

            String docId = r.getEventOwnerEmail() + "," + r.getAttendeeEmail() + "," + r.event.eventName;

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
        Log.d("LAYOUT", "THIS IS organizer_manage_event_activity PAGE");
        setContentView(R.layout.organizer_manage_event_activity);

        Button btnCreate = findViewById(R.id.btn_organizer_create_event);
        btnCreate.setOnClickListener(v -> createEventLayout());

        RecyclerView rv = findViewById(R.id.recycler_organizer_manage_event);
        rv.setLayoutManager(new LinearLayoutManager(this)); // THIS WAS MISSING!

        Database.getEvents(events -> runOnUiThread(() -> {
            rv.setAdapter(new EventAdapter(events));
        }));
    }

    private void createEventLayout() {
        Log.d("LAYOUT", "THIS IS organizer_add_event_activity PAGE");
        setContentView(R.layout.organizer_add_event_activity);

        Button btnSubmit = findViewById(R.id.btn_organizer_add_event_submit);
        EditText nameField = findViewById(R.id.text_organizer_add_event_name);
        EditText descField = findViewById(R.id.text_organizer_add_event_description);
        EditText feeField = findViewById(R.id.text_organizer_add_event_fee);
        EditText dateField = findViewById(R.id.text_organizer_add_event_date);
        EditText timeField = findViewById(R.id.text_organizer_add_event_time);
        RadioGroup categoryGroup = findViewById(R.id.radio_group_event_categories);

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

        Database.getCategory(categories -> runOnUiThread(() -> {
            for (Category c : categories) {
                RadioButton rb = new RadioButton(this);
                rb.setText(c.getCategory_name());
                categoryGroup.addView(rb);
            }
        }));

        btnSubmit.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String desc = descField.getText().toString().trim();
            String date = dateField.getText().toString().trim();
            String time = timeField.getText().toString().trim();

            float fee;
            try {
                fee = Float.parseFloat(feeField.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid fee", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = categoryGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = ((RadioButton) findViewById(selectedId)).getText().toString();
            Event event = new Event(name, desc, category, fee, date, time, (OrganizerUser) UserOperation.currentUser);
            EventOperation.addEvent(UserOperation.currentUser.getEmail(), event);
            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
            manageEventsLayout();
        });
    }

    private void editEventLayout(Event event) {
        setContentView(R.layout.organizer_add_event_activity);

        EditText nameField = findViewById(R.id.text_organizer_add_event_name);
        EditText descField = findViewById(R.id.text_organizer_add_event_description);
        EditText feeField = findViewById(R.id.text_organizer_add_event_fee);
        EditText dateField = findViewById(R.id.text_organizer_add_event_date);
        EditText timeField = findViewById(R.id.text_organizer_add_event_time);
        RadioGroup categoryGroup = findViewById(R.id.radio_group_event_categories);
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

        Database.getCategory(categories -> runOnUiThread(() -> {
            for (Category c : categories) {
                RadioButton rb = new RadioButton(this);
                rb.setText(c.getCategory_name());
                categoryGroup.addView(rb);
                if (c.getCategory_name().equals(event.associatedCategory)) {
                    rb.setChecked(true);
                }
            }
        }));

        btnSubmit.setOnClickListener(v -> {
            float fee;
            try {
                fee = Float.parseFloat(feeField.getText().toString().trim());
            } catch (Exception e) {
                Toast.makeText(this, "Invalid fee", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = categoryGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select a category", Toast.LENGTH_SHORT).show();
                return;
            }

            String newCategory = ((RadioButton) findViewById(selectedId)).getText().toString();

            EventOperation.deleteEvent(UserOperation.currentUser.getEmail(), event);
            Event updated = new Event(
                    nameField.getText().toString().trim(),
                    descField.getText().toString().trim(),
                    newCategory,
                    fee,
                    dateField.getText().toString().trim(),
                    timeField.getText().toString().trim(),
                    (OrganizerUser) UserOperation.currentUser
            );

            EventOperation.addEvent(UserOperation.currentUser.getEmail(), updated);
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            manageEventsLayout();
        });
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.VH> {

        private final List<Event> items;

        EventAdapter(List<Event> items) {
            this.items = items;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView name, description, fee, category, date, time;
            Button btnEdit, btnDelete;

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
                EventOperation.deleteEvent(UserOperation.currentUser.getEmail(), e);
                items.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
