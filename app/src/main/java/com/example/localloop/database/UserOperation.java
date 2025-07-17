package com.example.localloop.database;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import java.util.function.Consumer;
import com.example.localloop.usertype.User;

public class UserOperation {

    public static User currentUser; //STORE THE USER INFO AFTER LOGGED IN

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // refactored: returns via callbacks
    public static void signInUserAuthEmailAndPassword(
        String email,
        String password,
        Consumer<FirebaseUser> onSuccess,
        Consumer<Exception> onFailure
    ) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    if (user != null) {
                        Log.d("LOGIN", "User logged in: " + user.getUid());
                        onSuccess.accept(user);
                    } else {
                        onFailure.accept(new Exception("Authentication succeeded but user is null"));
                    }
                } else {
                    Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Unknown error");
                    onFailure.accept(e);
                }
            });
    }

    public static void signOutUserAuth() {
        mAuth.signOut();
        Log.d("LOGOUT", "User logged out");
    }

    public static void signUpUserAuth(
        User user,
        String password,
        Consumer<FirebaseUser> onSuccess,
        Consumer<Exception> onFailure
    ) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    String uid = firebaseUser != null ? firebaseUser.getUid() : null;
                    if (uid != null) {
                        user.setUid(uid);
                        Database.set("users", uid, user.toMap());
                        Log.d("SIGNUP", "User created and stored: " + uid);
                        onSuccess.accept(firebaseUser);
                    } else {
                        onFailure.accept(new Exception("Sign‐up succeeded but user is null"));
                    }
                } else {
                    Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Unknown error");
                    onFailure.accept(e);
                }
            });
    }

    public static void deleteUserAccount(User user) {
        Database.delete("users", user.getUid(), user.toMap());
    }

    public static void setEnableUserAccount(
        User user,
        boolean enable,
        Runnable onSuccess,
        Consumer<Exception> onFailure
    ) {
        // prevent disabling/enabling admins
        if ("admin".equals(user.user_role)) {
            onFailure.accept(new Exception("Cannot modify admin account status"));
            return;
        }
        user.setDisable(enable);
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.getUid())
            .set(user.toMap())
            .addOnSuccessListener(aVoid -> onSuccess.run())
            .addOnFailureListener(onFailure::accept);
    }
}