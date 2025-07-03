package com.example.localloop.Archive.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DbUtil {

    private static final String TAG = "DbUtil";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Add (creates new or overwrites if docId exists)
    public static void add(String collection, String docId, Map<String, Object> data) {
        db.collection(collection).document(docId).set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Document added/updated: " + docId))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    // Edit (similar to add, but intended for replacing the document)
    public static void edit(String collection, String docId, Map<String, Object> data) {
        add(collection, docId, data); // Same as add in Firestore
    }

    // Get
    public static void get(String collection, String docId, OnSuccessListener<DocumentSnapshot> onSuccess, OnFailureListener onFailure) {
        db.collection(collection).document(docId).get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    // Delete
    public static void delete(String collection, String docId) {
        db.collection(collection).document(docId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Document deleted: " + docId))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }
}
