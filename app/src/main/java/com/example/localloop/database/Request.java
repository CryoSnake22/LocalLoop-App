package com.example.localloop.database;

import com.example.localloop.usertype.ParticipantUser;

import java.util.HashMap;


public class Request {
    public Event event;
    public int requestStatus;
    public ParticipantUser attendee;
    private String requestId;

    public Request(ParticipantUser attendee, Event event) {
        this.attendee = attendee;
        this.event = event;

        this.requestId = event.getUniqueId() + "," + attendee.getUid();

        this.requestStatus = 0; //-1 = declined, 0 = pending, 1 = accepted
    }

    public Request(ParticipantUser attendee, Event event, String requestId) {
        this(attendee, event);
        this.requestId = requestId;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("request_id", this.requestId);
        fields.put("request_status", this.requestStatus);
        fields.put("attendee_uid", this.attendee.getUid());
        fields.put("attendee_email", this.attendee.user_email);
        fields.put("attendee_username", this.attendee.user_name);
        fields.put("attendee_firstname", this.attendee.first_name);
        fields.put("attendee_lastname", this.attendee.last_name);
        fields.put("event_name", this.event.eventName);
        fields.put("event_unique_id", this.event.getUniqueId());
        fields.put("event_owner_email", this.event.getEventOwnerEmail());
        fields.put("event_owner_uid", this.event.getEventOwnerUid());

        return fields;
    }

    public String getRequestId() { return this.requestId; }

    public ParticipantUser getAttendee() { return this.attendee; }

    public Event getEvent() { return this.event; }

    public int getRequestStatus() { return this.requestStatus; }

    public String getAttendeeEmail() {
        return this.attendee.user_email;
    }

    public String getEventOwnerEmail() {
        return this.event.getEventOwnerEmail();
    }

    public String getEventOwnerUid() { return this.event.getEventOwnerUid(); }

}
