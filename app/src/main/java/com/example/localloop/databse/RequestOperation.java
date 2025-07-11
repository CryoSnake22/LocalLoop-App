package com.example.localloop.databse;

public class RequestOperation {


    //Email represent event organizer
    public static void addEvent(Request request) {
        Database.add("requests", request.getEventOwnerEmail() + "," + request.getAttendeeEmail() + "," + request.event.getEventName(), request.toMap());
    }

    public static void deleteEvent(String email, Request request, Event event) {
        Database.delete("requests", request.getEventOwnerEmail() + "," + request.getAttendeeEmail() + "," + request.event.getEventName(), request.toMap());
    }

}
