package com.example.localloop.data.model;

public abstract class User {

    private static int totalUser;
    private static int nextId;
    private final int id;

    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Role role;

    public User(String firstName, String lastName, String userName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;

        this.id = nextId;
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

    public String getPassword() {
        return this.password;
        // To be changed later to "getPasswordHash" otherwise this shit gonna blow up
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
