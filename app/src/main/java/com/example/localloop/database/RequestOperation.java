package com.example.localloop.database;

public class RequestOperation {


    //Email represent event organizer
    public static void addRequest(Request request) {
        Database.set("requests", request.getRequestId(), request.toMap());
    }

    public static void deleteRequest(Request request) {
        Database.delete("requests", request.getRequestId());
    }

}
