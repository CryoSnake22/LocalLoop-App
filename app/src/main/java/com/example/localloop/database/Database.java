package com.example.localloop.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.localloop.usertype.AdminUser;
import com.example.localloop.usertype.OrganizerUser;
import com.example.localloop.usertype.ParticipantUser;
import com.example.localloop.usertype.User;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Database {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void set(String collectionPath, String documentPath, HashMap<String, Object>fields) {
        db.collection(collectionPath).document(documentPath).set(fields);
    }

    public static void delete(String collectionPath, String documentPath) {
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

    private static User parseUserFromMap(String uid, Map<String,Object> data) {
        if (data == null) return null;
        String email    = (String) data.get("user_email");
        String username     = (String) data.get("user_name");
        String role     = (String) data.get("user_role");
        String first    = (String) data.get("first_name");
        String last     = (String) data.get("last_name");
        boolean disabled = Boolean.TRUE.equals(data.get("user_disabled"));

        switch (role) {
            case "organizer":
                OrganizerUser ou = new OrganizerUser(email, username, role, first, last, uid);
                ou.setDisable(disabled);
                return ou;
            case "participant":
                ParticipantUser pu = new ParticipantUser(email, username, role, first, last, uid);
                pu.setDisable(disabled);
                return pu;
            case "admin":
                return new AdminUser(email, username, role, first, last, uid);
            default:
                return null;
        }
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
                    OrganizerUser user = new OrganizerUser(email, name, role, first, last, docId);
                    user.setDisable(disabled);
                    userList.add(user);
                } else if (role.equals("participant")) {
                    ParticipantUser user = new ParticipantUser(email, name, role, first, last, docId);
                    user.setDisable(disabled);
                    userList.add(user);
                } else if (role.equals("admin")) {
                    AdminUser user = new AdminUser(email, name, role, first, last, docId);
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

                if (role.equals("organizer")) {
                    OrganizerUser user = new OrganizerUser(email, name, role, first, last, docId);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("participant")) {
                    ParticipantUser user = new ParticipantUser(email, name, role, first, last, docId);
                    user.setDisable(disabled);
                    userList.add(user);
                }
                else if(role.equals("admin")) {
                    AdminUser user = new AdminUser(email, name, role, first, last, docId);
                    userList.add(user);
                }
                else {
                    continue;
                }

            }
        });

        return userList;
    }

    public static void getUserByUid(String uid, Consumer<User> callback) {
        get("users", uid, data -> {
            User u = parseUserFromMap(uid, data);
            callback.accept(u);
        });
    }

    public static void getUsersByUids(List<String> uids, Consumer<Map<String, User>> callback) {
        get("users", allData -> {
            Map<String, User> out = new HashMap<>();
            for (Map.Entry<String, Map<String,Object>> e : allData.entrySet()) {
                String docId = e.getKey();
                if (uids.contains(docId)) {
                    User u = parseUserFromMap(docId, e.getValue());
                    if (u != null) out.put(docId, u);
                }
            }
            callback.accept(out);
        });
    }

    private static Event parseEventFromMap(Map<String,Object> data, OrganizerUser owner) {
        if (data == null) return null;
        try {
            String uniqueId    = (String) data.get("unique_id");
            String name        = (String) data.get("event_name");
            String description = (String) data.get("event_description");
            String category    = (String) data.get("associated_category");
            float  fee         = Float.parseFloat(String.valueOf(data.get("event_fee")));
            String date        = (String) data.get("event_date");
            String time        = (String) data.get("event_time");
            // Construct with the real owner
            return new Event(name, description, category, fee, date, time, owner, uniqueId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse event " + data.get("unique_id"), e);
            return null;
        }
    }

    public static void getEventById(String eventId, Consumer<Event> callback) {

        // Grab the raw event document
        get("events", eventId, eventMap -> {
            if (eventMap == null) {
                callback.accept(null);
                return;
            }

            // Pull out the owner's UID and fetch that user
            String ownerUid = (String) eventMap.get("event_owner_uid");
            getUserByUid(ownerUid, user -> {
                OrganizerUser owner = null;
                if (user instanceof OrganizerUser) {
                    owner = (OrganizerUser) user;
                } else {
                    Log.w(TAG, "Owner UID " + ownerUid + " is not an OrganizerUser");
                }

                // Parse the rest of the event, wiring in the owner
                Event ev = parseEventFromMap(eventMap, owner);
                callback.accept(ev);
            });
        });
    }

    public static List<OrganizerUser> getOrganizers() {
        List<User> userList = getUsers();
        List<OrganizerUser> organizerList = new ArrayList<>();

        for (User user : userList) {
            if (user instanceof OrganizerUser) {
                organizerList.add((OrganizerUser) user);
            }
        }

        return organizerList;
    }

    public static List<ParticipantUser> getParticipants() {
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

                    String uniqueId = (String) eventData.get("unique_id");
                    String name = (String) eventData.get("event_name");
                    String description = (String) eventData.get("event_description");
                    String category = (String) eventData.get("associated_category");
                    String feeStr = String.valueOf(eventData.get("event_fee"));
                    String date = (String) eventData.get("event_date");
                    String time = (String) eventData.get("event_time");
                    String ownerEmail = (String) eventData.get("event_owner_email");
                    String ownerUid = (String) eventData.get("event_owner_uid");

                    // Skip if missing anything
                    if (uniqueId == null || name == null || description == null || category == null ||
                            feeStr == null || date == null || time == null || ownerEmail == null || ownerUid == null) {
                        Log.d("Database", "Skipping incomplete event");
                        continue;
                    }

                    float fee = Float.parseFloat(feeStr);

                    // Only show events owned by current organizer
                    if (UserOperation.currentUser instanceof OrganizerUser &&
                            ownerUid.equals(UserOperation.currentUser.getUid())) {

                        OrganizerUser owner = (OrganizerUser) UserOperation.currentUser;
                        Event event = new Event(name, description, category, fee, date, time, owner, uniqueId);
                        eventsList.add(event);
                        Log.d("Database", "Added event " + uniqueId + ": " + name);
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
        Database.get("events", data -> {
            if (data == null || data.isEmpty()) {
                callback.accept(Collections.emptyList());
                return;
            }
            AtomicInteger remaining = new AtomicInteger(data.size());
            List<Event> eventsList = Collections.synchronizedList(new ArrayList<>());

            for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
                Map<String, Object> eventData = entry.getValue();
                String ownerUid = (String) eventData.get("event_owner_uid");

                getUserByUid(ownerUid, user -> {
                    OrganizerUser owner = (user instanceof OrganizerUser) ? (OrganizerUser) user : null;
                    Event ev = parseEventFromMap(eventData, owner);
                    if (ev != null) {
                        eventsList.add(ev);
                    }
                    if (remaining.decrementAndGet() == 0) {
                        callback.accept(eventsList);
                    }
                });
            }
        });
    }


    public static void getAllRequests(Consumer<List<Request>> callback) {
        get("requests", raw -> {
            if (raw == null || raw.isEmpty()) {
                callback.accept(Collections.emptyList());
                return;
            }

            int total = raw.size();
            AtomicInteger remaining = new AtomicInteger(total);
            List<Request> requests = Collections.synchronizedList(new ArrayList<>());

            for (Map<String, Object> requestData : raw.values()) {
                // pull everything into final locals
                final String requestId     = (String) requestData.get("request_id");
                final String attendeeUid  = (String) requestData.get("attendee_uid");
                final String eventDocId   = (String) requestData.get("event_unique_id");
                final Number statusNum    = (Number) requestData.get("request_status");
                final int status          = statusNum != null ? statusNum.intValue() : 0;

                // 1) load the participant
                getUserByUid(attendeeUid, u -> {
                    // now participant is a single, final assignment
                    final ParticipantUser participant =
                            (u instanceof ParticipantUser) ? (ParticipantUser) u : null;

                    // 2) load the event (which itself wires its owner)
                    getEventById(eventDocId, ev -> {
                        if (participant != null && ev != null) {
                            Request req = new Request(participant, ev, requestId);
                            req.requestStatus = status;
                            requests.add(req);
                            Log.d(TAG, "Loaded request: " + participant.getEmail() + " for event: " + ev.getEventName());
                        } else {
                            Log.w(TAG, "Skipping invalid request: " + attendeeUid + " / " + eventDocId);
                        }

                        // 3) countdown and fire final callback when done
                        if (remaining.decrementAndGet() == 0) {
                            Log.d(TAG, "Total ALL requests loaded: " + requests.size());
                            callback.accept(requests);
                        }
                    });
                });
            }
        });
    }
}
