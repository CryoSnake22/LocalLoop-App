package com.example.localloop.databse;

public class UserOperation {

    public static User currentUser; //STORE THE USER INFO AFTER LOGGED IN

    public static void addUserAccount(User user) {
        Database.add("user", user.getEmail(), user.toMap());
    }

    public static void deleteUserAccount(User user) {
        Database.delete("user", user.getEmail(), user.toMap());
    }

    public static void disableUserAccount(User oldUser, User newUser) {
        deleteUserAccount(oldUser);
        addUserAccount(newUser);
    }

    public static void getAllUserAccount() {

    }
}
