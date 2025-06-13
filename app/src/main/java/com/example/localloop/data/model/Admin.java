package com.example.localloop.data.model;

public class Admin extends User {

    public Admin(String UID, boolean active, String firstName, String lastName, String userName, String email) {
        super(UID, active, firstName, lastName, userName, email, "ADMIN");
    }
}
