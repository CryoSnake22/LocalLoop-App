package com.example.localloop.database;

public class CategoryOperation {

    public static void addCategory(Category category) {
        Database.set("category", category.getCategory_name(), category.toMap());
    }

    public static void deleteCategory(Category category) {
        Database.delete("category", category.getCategory_name());

    }
}
