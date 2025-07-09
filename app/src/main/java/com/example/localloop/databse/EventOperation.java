package com.example.localloop.databse;

public class EventOperation {


    //Email represent event organizer
    public static void addEvent(String email, Event event) {
        Database.add("events", email + "," + event.getEventName(), event.toMap());
    }

    public static void deleteEvent(String email, Event event) {
        Database.delete("events", email + "," + event.getEventName(), event.toMap());
    }

}
