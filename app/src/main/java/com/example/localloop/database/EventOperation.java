package com.example.localloop.database;

public class EventOperation {


    //Email represent event organizer
    public static void addEvent(String email, Event event) {
        Database.set("events", email + "," + event.getEventName(), event.toMap());
    }

    public static void deleteEvent(String email, Event event) {
        Database.delete("events", email + "," + event.getEventName(), event.toMap());
    }

}
