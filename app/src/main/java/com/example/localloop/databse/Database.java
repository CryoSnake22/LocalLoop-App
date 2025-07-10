package com.example.localloop.databse;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.localloop.usertype.AdminUser;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;
import com.example.localloop.usertype.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Database {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void add(String collectionPath, String documentPath, HashMap<String, Object>fields) {
        db.collection(collectionPath).document(documentPath).set(fields);
    }

    public static void delete(String collectionPath, String documentPath, HashMap<String, Object>fields) {
        db.collection(collectionPath).document(documentPath).delete();
    }

    public static void get(String collectionPath, Consumer<Map<String, Map<String, Object>>> callback) {
        db.collection(collectionPath).get().addOnSuccessListener(qs -> {
            Map<String, Map<String, Object>> out = new HashMap<>();

            for (DocumentSnapshot doc : qs) {
                out.put(doc.getId(), doc.getData());
            }
            callback.accept(out);
        });
    }


    public static void get(String collectionPath, String documentId, Consumer<Map<String, Object>> callback) {
        db.collection(collectionPath)
                .document(documentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.accept(doc.getData());
                    } else {
                        callback.accept(null); // No such document
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching document", e);
                    callback.accept(null); // In case of error
                });
    }


    public static List<Category> getCategory() {
        List<Category> categoryList = new ArrayList<>();

        Database.get("user", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> categoryData = data.get(docId);

                String name = (String) categoryData.get("category_name");
                String description = (String) categoryData.get("category_description");

                Category category = new Category(name, description);
                categoryList.add(category);
            }
        });

        return categoryList;
    }


    public static List<User> getUser() {
        List<User> userList = new ArrayList<>();

        Database.get("user", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> userData = data.get(docId);

                String email = (String) userData.get("user_email");
                String name = (String) userData.get("user_name");
                String password = (String) userData.get("user_password");
                String role = (String) userData.get("user_role");
                String first = (String) userData.get("first_name");
                String last = (String) userData.get("last_name");

                boolean disabled = Boolean.TRUE.equals(userData.get("user_disabled"));


                if(role.equals("organizer")) {
                    OrganizerUser user = new OrganizerUser(email, name, password, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("participant")) {
                    ParticipantUser user = new ParticipantUser(email, name, password, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("admin")) {
                    AdminUser user = new AdminUser(email, name, password, role, first, last);
                    userList.add(user);
                }
                else {
                    continue;
                }

            }
        });

        return userList;
    }

    public static List<OrganizerUser> getOrganizer() {
        List<User> userList = getUser();
        List<OrganizerUser> organizerList = new ArrayList<>();

        for (User user : userList) {
            if (user instanceof OrganizerUser) {
                organizerList.add((OrganizerUser) user);
            }
        }

        return organizerList;
    }

    public static List<ParticipantUser> getParticipant() {
        List<User> userList = getUser();
        List<ParticipantUser> participantList = new ArrayList<>();

        for (User user : userList) {
            if (user instanceof ParticipantUser) {
                participantList.add((ParticipantUser) user);
            }
        }

        return participantList;
    }





    public static List<Event> getEvents() {
        List<Event> eventsList = new ArrayList<>();

        Database.get("user", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> eventData = data.get(docId);

                String name = (String) eventData.get("event_name");
                String description = (String) eventData.get("event_description");
                String category = (String) eventData.get("associated_category");
                String fee = (String) eventData.get("event_fee");
                String date = (String) eventData.get("event_date");
                String time = (String) eventData.get("event_time");
                String ownerEmail = (String) eventData.get("event_owner_email");

                List<OrganizerUser> organizerList = new ArrayList<>();

                for (OrganizerUser user : organizerList) {
                    if (user.getEmail().equals(ownerEmail)) {
                        Event event = new Event(name, description, category, Float.valueOf(fee), date, time, user);
                        eventsList.add(event);
                        break;
                    }
                }


            }
        });

        return eventsList;
    }


}
