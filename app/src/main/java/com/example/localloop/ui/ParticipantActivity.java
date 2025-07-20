package com.example.localloop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.localloop.database.Request;
import com.example.localloop.database.RequestOperation;
import com.example.localloop.database.UserOperation;
import com.example.localloop.usertype.ParticipantUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ParticipantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        participantHomeLayout();
    }

    private void participantHomeLayout() {
        setContentView(R.layout.participant_home_activity);

        Button btnSearch = findViewById(R.id.btn_participant_searchevents_submit);
        EditText searchBar = findViewById(R.id.text_participant_searchbar);
        RadioGroup categoryRadio = findViewById(R.id.radio_group_category_filter);
        RecyclerView recycler = findViewById(R.id.recycler_participant_events);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        searchBar.setOnClickListener(v->{
            categoryRadio.setVisibility(View.VISIBLE);
        });
        // Add default radio All Categories
        RadioButton allButton = new RadioButton(this);
        allButton.setText("All Categories");
        allButton.setId(View.generateViewId());
        categoryRadio.addView(allButton);
        allButton.setChecked(true);  // default selection

        // Load categories
        Database.getCategory(categories -> {
            runOnUiThread(() -> {
                for (Category category : categories) {
                    RadioButton rb = new RadioButton(this);
                    rb.setText(category.getCategory_name());
                    rb.setId(View.generateViewId());
                    categoryRadio.addView(rb);
                }

            });
        });

        // search
        btnSearch.setOnClickListener(v -> {
            int categoryId = categoryRadio.getCheckedRadioButtonId();
            String search = searchBar.getText().toString().trim();
            String category = "All Categories";

            if (categoryId != -1) {
                RadioButton selectedRadio = findViewById(categoryId);
                category = selectedRadio.getText().toString();
            }

            final String finalSearch = search.toLowerCase();
            final String finalCategory = category;

            Database.getAllEvents(events -> {
                runOnUiThread(() -> {
                    List<Event> result = new ArrayList<>();

                    for (Event e : events) {
                        boolean matchSearch = e.eventName.toLowerCase().contains(finalSearch);
                        boolean matchCategory = e.associatedCategory.equalsIgnoreCase(finalCategory);

                        if (!finalSearch.isEmpty() && !finalCategory.equals("All Categories")) {
                            if (matchSearch && matchCategory) result.add(e);
                        } else if (!finalSearch.isEmpty()) {
                            if (matchSearch) result.add(e);
                        } else if (!finalCategory.equals("All Categories")) {
                            if (matchCategory) result.add(e);
                        } else {
                            result.add(e);
                        }
                    }

                    recycler.setAdapter(new EventAdaptor(result));
                });
            });

            if (!search.isEmpty()) {
                Toast.makeText(this, "Search: " + search, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Category: " + category, Toast.LENGTH_SHORT).show();
            }
            categoryRadio.setVisibility(View.GONE);

            // input manager black magic to turn off the keyboard when you click search
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && v != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        // initial load: show all events on startup
        Database.getAllEvents(events -> runOnUiThread(() ->
                recycler.setAdapter(new EventAdaptor(events))
        ));

        //logout button
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());


    }

    private void logout() {
        UserOperation.signOutUserAuth(); // signs out and sets currentUser to null
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // closes the current activity
    }



    private class EventAdaptor extends RecyclerView.Adapter<EventAdaptor.VH> {

        private final List<Event> items;

        EventAdaptor(List<Event> items) {
            this.items = items;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView name, description, fee, category, date, time, owner;
            Button btnJoin;

            VH(View itemView) {
                super(itemView);
                owner = itemView.findViewById(R.id.text_participant_event_owner);
                name = itemView.findViewById(R.id.text_participant_event_name);
                description = itemView.findViewById(R.id.text_participant_event_description);
                fee = itemView.findViewById(R.id.text_participant_event_fee);
                category = itemView.findViewById(R.id.text_participant_event_category);
                date = itemView.findViewById(R.id.text_participant_event_date);
                time = itemView.findViewById(R.id.text_participant_event_time);
                btnJoin = itemView.findViewById(R.id.btn_participant_event_join_request);
            }
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant_events, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Event e = items.get(position);

            holder.owner.setText("Owner: " + e.getEventOwnerEmail());
            holder.name.setText(e.eventName);
            holder.description.setText(e.description);
            holder.fee.setText("Fee: $" + e.eventFee);
            holder.category.setText("Category: " + e.associatedCategory);
            holder.date.setText("Date: " + e.eventDate);
            holder.time.setText("Time: " + e.eventTime);

            String eventId = e.getUniqueId();

            Database.get("requests", eventId, data -> {
                if (data != null && data.containsKey("requestStatus")) {
                    Object rawStatus = data.get("requestStatus");
                    int status = rawStatus instanceof Long ? ((Long) rawStatus).intValue() : -99;
                    String statusText;

                    switch (status) {
                        case 0:
                            statusText = "Pending";
                            break;
                        case 1:
                            statusText = "Accepted";
                            break;
                        case -1:
                            statusText = "Declined";
                            break;
                        default:
                            statusText = "Requested";
                    }

                    holder.btnJoin.setText(statusText);
                    holder.btnJoin.setEnabled(false);
                } else {
                    holder.btnJoin.setText("Join");
                    holder.btnJoin.setEnabled(true);
                    holder.btnJoin.setOnClickListener(v -> {
                        Request request = new Request((ParticipantUser) UserOperation.currentUser, e);
                        RequestOperation.addRequest(request);
                        Toast.makeText(v.getContext(), "Join request sent", Toast.LENGTH_SHORT).show();
                        holder.btnJoin.setText("Pending");
                        holder.btnJoin.setEnabled(false);
                    });
                }
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
        startActivity(new Intent(this, LoginActivity.class));
    }


}
