package com.example.localloop.database;

public class RequestOperation {


    //Email represent event organizer
    public static void addRequest(Request request) {
        Database.set("requests", request.getEventOwnerEmail() + "," + request.getAttendeeEmail() + "," + request.event.getEventName(), request.toMap());
    }

    public static void deleteRequest(String email, Request request, Event event) {
        Database.delete("requests", request.getEventOwnerEmail() + "," + request.getAttendeeEmail() + "," + request.event.getEventName());
    }

}
