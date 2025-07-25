package com.example.localloop.Archive.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.localloop.Archive.data.model.Admin;
import com.example.localloop.Archive.data.model.Organizer;
import com.example.localloop.Archive.data.model.Participant;
import com.example.localloop.Archive.data.model.User;
import com.example.localloop.data.model.*;
import com.example.localloop.ui.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {

    private static final String TAG = "LoginActivity";

    public interface UserCallback {

        void onUserLoaded(User user);
    }

    static public void UIDtoUserAsync(String UID, UserCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String firstName = document.get("firstName").toString();
                        String lastName = document.get("lastName").toString();
                        String userName = document.get("userName").toString();
                        String email = document.get("email").toString();
                        String role = document.get("role").toString();
                        User user = null;
                        if (role.equals("admin")) {
                            user = new Admin(UID, true, firstName, lastName, userName, email);
                        } else if (role.equals("participant")) {
                            user = new Participant(UID, true, firstName, lastName, userName, email);

                        } else if (role.equals("organizer")) {
                            user = new Organizer(UID, true, firstName, lastName, userName, email);
                        }
                        if (user != null) {
                            callback.onUserLoaded(user);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        callback.onUserLoaded(null);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    callback.onUserLoaded(null);
                }
            }
        });
    }
}
