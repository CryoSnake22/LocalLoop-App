package com.example.localloop.database;

public class EventOperation {

    public static void addEvent(Event event) {
        Database.set("events", event.getUniqueId(), event.toMap());
    }

    public static void updateEvent(Event event, Event update) {
        event.update(update);
        Database.set("events", event.getUniqueId(), event.toMap());
    }

    public static void deleteEvent(Event event) {
        Database.delete("events", event.getUniqueId());
    }

}
