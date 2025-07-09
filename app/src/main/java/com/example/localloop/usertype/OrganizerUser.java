package com.example.localloop.usertype;

import com.example.localloop.databse.Event;
import com.example.localloop.databse.EventOperation;
import com.example.localloop.databse.User;

public class OrganizerUser extends User {

    public OrganizerUser(String user_email, String user_name, String user_password, String user_role, String first_name, String last_name) {
        super(user_email, user_name, user_password, user_role, first_name, last_name);
    }

    public void addEvent(Event event) {
        EventOperation.addEvent(this.user_email, event);
    }
}
