package com.example.localloop.database;

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

    public static void set(String collectionPath, String documentPath, HashMap<String, Object>fields) {
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


/*    public static List<Category> getCategory() {
        List<Category> categoryList = new ArrayList<>();

        Database.get("category", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> categoryData = data.get(docId);

                String name = (String) categoryData.get("category_name");
                String description = (String) categoryData.get("category_description");

                Category category = new Category(name, description);
                categoryList.add(category);
            }
        });

        return categoryList;
    }*/

    public static void getCategory(Consumer<List<Category>> callback) {
        List<Category> categoryList = new ArrayList<>();

        Database.get("category", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> categoryData = data.get(docId);

                String name = (String) categoryData.get("category_name");
                String description = (String) categoryData.get("category_description");

                Category category = new Category(name, description);
                categoryList.add(category);
            }

            callback.accept(categoryList);
        });
    }


    public static void getUsersSync(Consumer<List<User>> callback) {
        List<User> userList = new ArrayList<>();

        Database.get("users", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> userData = data.get(docId);

                String email = (String) userData.get("user_email");
                String name = (String) userData.get("user_name");
                String role = (String) userData.get("user_role");
                String first = (String) userData.get("first_name");
                String last = (String) userData.get("last_name");

                boolean disabled = Boolean.TRUE.equals(userData.get("user_disabled"));

                if (role.equals("organizer")) {
                    OrganizerUser user = new OrganizerUser(email, name, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                } else if (role.equals("participant")) {
                    ParticipantUser user = new ParticipantUser(email, name, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                } else if (role.equals("admin")) {
                    AdminUser user = new AdminUser(email, name, role, first, last);
                    userList.add(user);
                }
            }

            callback.accept(userList);
        });
    }

    public static List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        Database.get("users", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> userData = data.get(docId);

                String email = (String) userData.get("user_email");
                String name = (String) userData.get("user_name");
                String role = (String) userData.get("user_role");
                String first = (String) userData.get("first_name");
                String last = (String) userData.get("last_name");

                boolean disabled = Boolean.TRUE.equals(userData.get("user_disabled"));


                if(role.equals("organizer")) {
                    OrganizerUser user = new OrganizerUser(email, name, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("participant")) {
                    ParticipantUser user = new ParticipantUser(email, name, role, first, last);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("admin")) {
                    AdminUser user = new AdminUser(email, name, role, first, last);
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
        List<User> userList = getUsers();
        List<OrganizerUser> organizerList = new ArrayList<>();

        for (User user : userList) {
            if (user instanceof OrganizerUser) {
                organizerList.add((OrganizerUser) user);
            }
        }

        return organizerList;
    }

    public static List<ParticipantUser> getParticipant() {
        List<User> userList = getUsers();
        List<ParticipantUser> participantList = new ArrayList<>();

        for (User user : userList) {
            if (user instanceof ParticipantUser) {
                participantList.add((ParticipantUser) user);
            }
        }

        return participantList;
    }





/*    public static List<Event> getEvents() {
        List<Event> eventsList = new ArrayList<>();

        Database.get("users", data -> {
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
    }*/



    public static void getEvents(Consumer<List<Event>> callback) {
        List<Event> eventsList = new ArrayList<>();

        Database.get("events", data -> {
            Log.d("Database", "Fetched events size: " + data.size());  // ADD THIS

            for (String docId : data.keySet()) {
                try {
                    Map<String, Object> eventData = data.get(docId);
                    Log.d("Database", "Raw event: " + eventData); // ADD THIS

                    String name = (String) eventData.get("event_name");
                    String description = (String) eventData.get("event_description");
                    String category = (String) eventData.get("associated_category");
                    String feeStr = String.valueOf(eventData.get("event_fee"));
                    String date = (String) eventData.get("event_date");
                    String time = (String) eventData.get("event_time");
                    String ownerEmail = (String) eventData.get("event_owner_email");

                    // Skip if missing anything
                    if (name == null || description == null || category == null ||
                            feeStr == null || date == null || time == null || ownerEmail == null) {
                        Log.d("Database", "Skipping incomplete event");
                        continue;
                    }

                    float fee = Float.parseFloat(feeStr);

                    // Only show events owned by current organizer
                    if (UserOperation.currentUser instanceof OrganizerUser &&
                            ownerEmail.equals(UserOperation.currentUser.getEmail())) {

                        OrganizerUser owner = (OrganizerUser) UserOperation.currentUser;
                        Event event = new Event(name, description, category, fee, date, time, owner);
                        eventsList.add(event);
                        Log.d("Database", "Added event: " + name);
                    } else {
                        Log.d("Database", "Skipped event (owner mismatch): " + ownerEmail);
                    }

                } catch (Exception e) {
                    Log.e("Database", "Error parsing event: " + e.getMessage());
                }
            }

            callback.accept(eventsList);
        });
    }

    public static void getAllEvents(Consumer<List<Event>> callback) {
        List<Event> eventsList = new ArrayList<>();

        Database.get("events", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> eventData = data.get(docId);

                String name = (String) eventData.get("event_name");
                String description = (String) eventData.get("event_description");
                String category = (String) eventData.get("associated_category");
                String feeStr = String.valueOf(eventData.get("event_fee"));
                String date = (String) eventData.get("event_date");
                String time = (String) eventData.get("event_time");
                String ownerEmail = (String) eventData.get("event_owner_email");

                float fee = 0;
                try {
                    fee = Float.parseFloat(feeStr);
                } catch (Exception ignored) {}

                Event event = new Event(name, description, category, fee, date, time, new OrganizerUser(ownerEmail, "", "", "", ""));
                eventsList.add(event);

                Log.d("Database", "Loaded ALL event: " + name + " by " + ownerEmail);
            }

            Log.d("Database", "Total ALL events loaded: " + eventsList.size());
            callback.accept(eventsList);
        });
    }


    public static void getAllRequests(Consumer<List<Request>> callback) {
        List<Request> requestList = new ArrayList<>();

        Database.get("requests", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> requestData = data.get(docId);

                String attendeeEmail = (String) requestData.get("attendee_email");
                String attendeeUsername = (String) requestData.get("attendee_username");
                String attendeeFirst = (String) requestData.get("attendee_firstname");
                String attendeeLast = (String) requestData.get("attendee_lastname");
                String eventName = (String) requestData.get("event_name");
                String eventOwnerEmail = (String) requestData.get("event_owner_email");
                Object statusObj = requestData.get("request_status");

                if (attendeeEmail == null || eventName == null || eventOwnerEmail == null || statusObj == null)
                    continue;

                int status = 0;
                try {
                    status = ((Number) statusObj).intValue();
                } catch (Exception ignored) {}

                ParticipantUser participant = new ParticipantUser(attendeeEmail, attendeeUsername, "participant", attendeeFirst, attendeeLast);
                OrganizerUser organizer = new OrganizerUser(eventOwnerEmail, "", "", "", "");
                Event dummyEvent = new Event(eventName, "", "", 0f, "", "", organizer);

                Request request = new Request(participant, dummyEvent);
                request.requestStatus = status;

                requestList.add(request);

                Log.d("Database", "Loaded request: " + attendeeEmail + " for event: " + eventName);
            }

            Log.d("Database", "Total ALL requests loaded: " + requestList.size());
            callback.accept(requestList);
        });
    }


}
