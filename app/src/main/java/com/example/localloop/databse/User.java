package com.example.localloop.databse;

import java.util.HashMap;

public class User {
    public String user_email;
    public String user_name;
    public String user_password;

    public String user_role;
    public String first_name;
    public String last_name;

    private boolean disabled;

    public User(String user_email, String user_name, String user_password, String user_role, String first_name, String last_name) {

        this.user_email = user_email;
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_role = user_role;
        this.first_name = first_name;
        this.last_name = last_name;

        this.disabled = false;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("user_email", this.user_email);
        fields.put("user_name", this.user_name);
        fields.put("user_password", this.user_password);
        fields.put("user_role", this.user_role);
        fields.put("first_name", this.first_name);
        fields.put("last_name", this.last_name);
        fields.put("user_disabled", this.disabled);

        return fields;
    }

    public String getUserName() {
        return user_name;
    }

    public void setDisable(boolean disabled) {
        this.disabled = disabled;
    }

    public String getEmail() {
        return user_email;
    }
}
