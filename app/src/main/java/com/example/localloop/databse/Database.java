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


import java.util.HashMap;
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
            for (DocumentSnapshot doc : qs) out.put(doc.getId(), doc.getData());
            callback.accept(out);
        });
    }
}
