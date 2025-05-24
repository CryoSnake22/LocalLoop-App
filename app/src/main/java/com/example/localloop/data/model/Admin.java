package com.example.localloop.data.model;

public class Admin extends User {

    public Admin(String firstName, String lastName, String userName, String email) {
        super(firstName, lastName, userName, email, Role.ADMIN);
    }
}
