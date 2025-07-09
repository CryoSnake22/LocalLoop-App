package com.example.localloop.databse;

import com.example.localloop.usertype.OrganizerUser;

import java.util.HashMap;

public class Event {
    public String eventName;
    public String description;
    public String associatedCategory;
    public String eventDate;
    public String eventTime;
    public float eventFee;
    private int id;

    private User eventOwner;

    private static int eventId = 1;

    public Event(String eventName, String description, String associatedCategory, float eventFee, String eventDate, String eventTime, OrganizerUser eventOwner) {
        this.eventName = eventName;
        this.description = description;
        this.associatedCategory = associatedCategory;
        this.eventFee = eventFee;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventOwner = eventOwner;
        this.id = eventId;
        eventId++;

        eventOwner.addEvent(this);
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("event_name", this.eventName);
        fields.put("event_description", this.description);
        fields.put("event_fee", this.eventFee);
        fields.put("event_date", this.eventDate);
        fields.put("event_time", this.eventTime);
        fields.put("event_id", this.id);

        fields.put("associated_category", this.associatedCategory);

        fields.put("event_owner", eventOwner.getEmail());

        return fields;
    }

    public String getEventName() {
        return eventName;
    }

    public String getId(){
        return Integer.toString(id);
    }
}
