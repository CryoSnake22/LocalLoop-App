package com.example.localloop.usertype;

import java.util.HashMap;

public abstract class User {
    private String uid;              // add unique user ID

    public String user_email;
    public String user_name;
    public String user_role;
    public String first_name;
    public String last_name;

    private boolean disabled;

    public User(String user_email, String user_name, String user_role, String first_name, String last_name, String uid) {

        this.user_email = user_email;
        this.user_name = user_name;
        this.user_role = user_role;
        this.first_name = first_name;
        this.last_name = last_name;
        this.uid = uid;

        this.disabled = false;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("uid", this.uid);
        fields.put("user_email", this.user_email);
        fields.put("user_name", this.user_name);
        fields.put("user_role", this.user_role);
        fields.put("first_name", this.first_name);
        fields.put("last_name", this.last_name);
        fields.put("user_disabled", this.disabled);

        return fields;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return user_email;
    }

    public String getUserName() {
        return user_name;
    }

    public String getUserRole() {
        return user_role;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisable(boolean disabled) {
        this.disabled = disabled;
    }


}