package com.example.localloop.Archive.data.model;

public class Participant extends User {

    public Participant(String UID, boolean active, String firstName, String lastName, String userName, String email) {
        super(UID, active, firstName, lastName, userName, email, "PARTICIPANT");
    }
}
