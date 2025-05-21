package com.example.localloop.data.model;

public class Organizer extends User {

    public Organizer(String firstName, String lastName, String userName, String email, String password) {
        super(firstName, lastName, userName, email, password, Role.ORGANIZER);
    }
}
