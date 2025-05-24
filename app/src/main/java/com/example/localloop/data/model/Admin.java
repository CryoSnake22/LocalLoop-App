package com.example.localloop.data.model;

public class Admin extends User {

    public Admin(String UID,String firstName, String lastName, String userName, String email) {
        super(UID,firstName, lastName, userName, email, Role.ADMIN);
    }
}
