package com.example.localloop.databse;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;


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
                String role = (String) userData.get("user_role");
                String first = (String) userData.get("first_name");
                String last = (String) userData.get("last_name");
                boolean disabled = Boolean.TRUE.equals(userData.get("user_disabled"));

                User user = new User(email, name, "", role, first, last);
                user.setDisable(disabled);
                userList.add(user);
            }
        });

        return userList;
    }


    public static List<Event> getEvents() {
        List<Event> eventsList = new ArrayList<>();

        Database.get("user", data -> {
            for (String docId : data.keySet()) {
                Map<String, Object> userData = data.get(docId);

                String name = (String) userData.get("event_name");
                String description = (String) userData.get("event_description");
                String category = (String) userData.get("associated_category");
                String fee = (String) userData.get("event_fee");
                String date = (String) userData.get("event_date");
                String time = (String) userData.get("event_time");

                Event event = new Event(name, description, new Category(category, ""), Float.valueOf(fee), date, time);

                eventsList.add(event);
            }
        });

        return eventsList;
    }

}
