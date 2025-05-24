package com.example.localloop.data.model;

public class Organizer extends User {

    public Organizer(String UID,String firstName, String lastName, String userName, String email) {
        super(UID,firstName, lastName, userName, email, Role.ORGANIZER);
    }
}
