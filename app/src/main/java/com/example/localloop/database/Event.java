package com.example.localloop.database;

import android.util.Log;

import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Event {

    public String eventName;
    public String description;
    public String associatedCategory;
    public String eventDate;
    public String eventTime;
    public float eventFee;

    private OrganizerUser eventOwner;
    private String uniqueId;



    public List<ParticipantUser> eventRequest;
    public List<ParticipantUser> eventParticipant;


    private static int eventId = 1;

    public Event(String eventName, String description, String associatedCategory, float eventFee, String eventDate, String eventTime, OrganizerUser eventOwner) {
        this.eventName = eventName;
        this.description = description;
        this.associatedCategory = associatedCategory;
        this.eventFee = eventFee;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventOwner = eventOwner;
        eventId++;
        if (eventOwner !=null){
            this.uniqueId = eventOwner.getUid() + "." + String.valueOf(System.currentTimeMillis());
            eventOwner.createEvent(this);
        } else{
            Log.e("Event", "NULL eventOwner problem");
        }

    }

    public Event(String eventName, String description, String associatedCategory, float eventFee, String eventDate, String eventTime, OrganizerUser eventOwner, String uniqueId) {
        this(eventName, description, associatedCategory, eventFee, eventDate, eventTime, eventOwner);
        this.uniqueId = uniqueId;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("unique_id", this.uniqueId);
        fields.put("event_name", this.eventName);
        fields.put("event_description", this.description);
        fields.put("event_fee", this.eventFee);
        fields.put("event_date", this.eventDate);
        fields.put("event_time", this.eventTime);

        fields.put("associated_category", this.associatedCategory);

        fields.put("event_owner_email", eventOwner.getEmail());
        fields.put("event_owner_uid", eventOwner.getUid());

        return fields;
    }

    public String getEventName() {
        return eventName;
    }


    public void addEventRequest(ParticipantUser user) {
        eventRequest.add(user);
    }

    public void addParticipant(ParticipantUser user) {
        eventParticipant.add(user);
    }

    public String getEventOwnerUid() { return this.eventOwner.getUid(); }

    public String getEventOwnerEmail() {
        return this.eventOwner.user_email;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void getAttendees(Consumer<List<ParticipantUser>> callback) {
        List<ParticipantUser> attendees = new ArrayList<>();

        Database.getAllRequests(requests -> {
            if (requests != null) {
                for (Request request : requests) {
                    if (request == null) continue;
                    if (request.getAttendee() == null || request.getEvent() == null) continue;
                    if (request.getEvent().getUniqueId().equals(this.uniqueId) && request.getRequestStatus() == 1) {
                        attendees.add(request.getAttendee());
                    }
                }
            }
            callback.accept(attendees);
        });
    }

    public void update(Event update) {
        this.eventName = update.eventName;
        this.description = update.description;
        this.associatedCategory = update.associatedCategory;
        this.eventFee = update.eventFee;
        this.eventDate = update.eventDate;
        this.eventTime = update.eventTime;
    }
}
