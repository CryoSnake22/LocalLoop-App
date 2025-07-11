package com.example.localloop.databse;

import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Event {
    public String eventName;
    public String description;
    public String associatedCategory;
    public String eventDate;
    public String eventTime;
    public float eventFee;

    private OrganizerUser eventOwnerEmail;



    public List<ParticipantUser> eventRequest;
    public List<ParticipantUser> eventParticipant;


    private static int eventId = 1;

    public Event(String eventName, String description, String associatedCategory, float eventFee, String eventDate, String eventTime, OrganizerUser eventOwnerEmail) {
        this.eventName = eventName;
        this.description = description;
        this.associatedCategory = associatedCategory;
        this.eventFee = eventFee;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventOwnerEmail = eventOwnerEmail;
        eventId++;

        eventOwnerEmail.createEvent(this);
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("event_name", this.eventName);
        fields.put("event_description", this.description);
        fields.put("event_fee", this.eventFee);
        fields.put("event_date", this.eventDate);
        fields.put("event_time", this.eventTime);

        fields.put("associated_category", this.associatedCategory);

        fields.put("event_owner_email", eventOwnerEmail.getEmail());

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

    public String getEventOwnerEmail() {
        return this.eventOwnerEmail.user_email;
    }
}
