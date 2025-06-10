package com.example.localloop.data.model;

public abstract class User {
    private boolean active;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String role;
    private String UID;

    public User(String UID, boolean active, String firstName, String lastName, String userName, String email, String role) {
        this.active = active;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.UID = UID;
    }
    public String getUserName()    { return userName; }
    public void setUserName(String u) { this.userName = u; }
    public String getFirstName()   { return firstName; }
    public void setFirstName(String f) { this.firstName = f; }
    public String getLastName()    { return lastName; }
    public void setLastName(String l) { this.lastName = l; }
    public String getEmail()       { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getRole()        { return this.role; }
    public void setRole(String r)  { this.role = r; }
    public boolean isActive()      { return active; }
    public void setActive(boolean a) { this.active = a; }
    public String getUID() {
        return this.UID;
    }

    public boolean equals(Object o) {
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
