package com.example.localloop.database;

import com.example.localloop.usertype.ParticipantUser;

import java.util.HashMap;


public class Request {
    public Event event;
    public int requestStatus;

    public ParticipantUser attendee;

    public Request(ParticipantUser attendee, Event event) {

        this.attendee = attendee;
        this.event = event;

        this.requestStatus = 0; //-1 = delined, 0 = pending, 1 = accepted
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("request_status", this.requestStatus);
        fields.put("attendee_email", this.attendee.user_email);
        fields.put("attendee_username", this.attendee.user_name);
        fields.put("attendee_firstname", this.attendee.first_name);
        fields.put("attendee_lastname", this.attendee.last_name);
        fields.put("event_name", this.event.eventName);
        fields.put("event_owner_email", this.event.getEventOwnerEmail());

        return fields;
    }

    public String getAttendeeEmail() {
        return this.attendee.user_email;
    }

    public String getEventOwnerEmail() {
        return this.event.getEventOwnerEmail();
    }

}
