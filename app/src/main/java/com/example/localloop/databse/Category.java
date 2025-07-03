package com.example.localloop.databse;

import java.util.HashMap;

public class Category {
    public String category_name;
    public String category_description;
    private int id;

    private static int categoryId = 1;

    public Category(String category_name, String category_description) {

        this.category_name = category_name;
        this.category_description = category_description;

        this.id = categoryId;
        categoryId++;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> fields = new HashMap<>();

        fields.put("category_name", this.category_name);
        fields.put("category_description", this.category_description);

        return fields;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getId() {
        return Integer.toString(id);
    }
}
