package com.example.localloop.databse;

public class UserOperation {
    public static void addUserAccount(User user) {
        Database.add("account", user.getUserName(), user.toMap());
    }

    public static void deleteUserAccount(User user) {
        Database.delete("account", user.getUserName(), user.toMap());
    }

    public static void disableUserAccount(User oldUser, User newUser) {
        deleteUserAccount(oldUser);
        addUserAccount(newUser);
    }

    public static void getAllUserAccount() {

    }
}
