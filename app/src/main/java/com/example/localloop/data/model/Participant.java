package com.example.localloop.data.model;

public class Participant extends User {

    public Participant(String UID,String firstName, String lastName, String userName, String email) {
        super(UID,firstName, lastName, userName, email, Role.PARTICIPANT);
    }
}
