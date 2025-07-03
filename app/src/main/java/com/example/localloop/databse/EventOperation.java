package com.example.localloop.databse;

public class EventOperation {
    public static void addEvent(Event event) {
        Database.add("events", event.getEventName(), event.toMap());
    }

    public static void deleteEvent(Event event) {
        Database.delete("events", event.getEventName(), event.toMap());
    }

}
