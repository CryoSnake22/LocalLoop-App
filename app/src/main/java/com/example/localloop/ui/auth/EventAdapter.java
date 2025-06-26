package com.example.localloop.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localloop.R;
import com.example.localloop.data.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Adapter class to populate a RecyclerView with Event data
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;  // List of events to display
    private Context context;
    private Map<String, String> categoryMap = new HashMap<>();


// Getter and setter for categoryMap
    public void setCategoryMap(Map<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }


    // Constructor to initialize adapter with event data and context
    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    // ViewHolder holds references to each view inside a single event item
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventCategory, tvEventDateTime;
        Button btnEditEvent, btnDeleteEvent;

        public EventViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventCategory = itemView.findViewById(R.id.tvEventCategory);
            tvEventDateTime = itemView.findViewById(R.id.tvEventDateTime);
            btnEditEvent = itemView.findViewById(R.id.btnEditEvent);
            btnDeleteEvent = itemView.findViewById(R.id.btnDeleteEvent);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single event card from XML layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Get the event at the current position
        Event event = eventList.get(position);

        // Bind event data to UI elements
        holder.tvEventName.setText(event.getName());
        String categoryName = categoryMap.getOrDefault(event.getCategoryId(), "Unknown Category");
        holder.tvEventCategory.setText("Category: " + categoryName);

        holder.tvEventDateTime.setText(event.getDate() + " at " + event.getTime());

        // edit/delete functionality

        // DELETE button functionality
        holder.btnDeleteEvent.setOnClickListener(v -> {
            // Show confirmation dialog
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Get the Firestore document ID
                        String eventId = event.getId();

                        // Delete the document from Firestore
                        FirebaseFirestore.getInstance()
                                .collection("events")
                                .document(eventId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Remove from local list and refresh RecyclerView
                                    eventList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, eventList.size());

                                    // Show success message
                                    Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {

                                    // Show error message if failed
                                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // edit button functionality

        holder.btnEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEventActivity.class);
            intent.putExtra("eventId", event.getId()); // Pass event ID to edit screen
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return eventList.size();  // Return the number of events
    }


}
