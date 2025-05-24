package com.example.localloop.data.model;

public class Participant extends User {

    public Participant(String firstName, String lastName, String userName, String email) {
        super(firstName, lastName, userName, email, Role.PARTICIPANT);
    }
}
