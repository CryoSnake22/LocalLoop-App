package com.example.localloop.usertype;

import com.example.localloop.database.Event;
import com.example.localloop.database.EventOperation;

import java.util.ArrayList;
import java.util.List;

public class OrganizerUser extends User {

    public List<Event> eventsList;

    public OrganizerUser(String user_email, String user_name, String user_role, String first_name, String last_name, String uid) {
        super(user_email, user_name, user_role, first_name, last_name, uid);
        this.eventsList = new ArrayList<>();

    }

    public void createEvent(Event event) {
        eventsList.add(event);
        EventOperation.addEvent(event);
    }

    public List<ParticipantUser> getJoinRequest(Event event) {
        return event.eventRequest;
    }

}
