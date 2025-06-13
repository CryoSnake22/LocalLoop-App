package com.example.localloop.data.model;

public class Organizer extends User {

    public Organizer(String UID, boolean active, String firstName, String lastName, String userName, String email) {
        super(UID, active, firstName, lastName, userName, email, "ORGANIZER");
    }
}
