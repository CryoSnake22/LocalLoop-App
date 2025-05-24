package com.example.localloop.data.model;

public abstract class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Role role;
    private String UID;

    public User(String UID, String firstName, String lastName, String userName, String email, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.UID = UID;
    }
    public Role getRole() {
        return this.role;
    }

    public String getUsername() {
        return this.userName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }
    public String getUID() {
        return this.UID;
    }

    public boolean Equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return true;
        // TO BE FIXED, NOT FUNCTIONAL CURRENTLY
    }

}
