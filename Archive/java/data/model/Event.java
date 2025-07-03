package com.example.localloop.Archive.data.model;

public class Event {

    private String id;
    private String organizerId;
    private String name;
    private String description;
    private String categoryId;
    private double fee;
    private String date;
    private String time;


    // No arg constructor (for firestore)
    public Event() {}



    // Constructor
    public Event(String id, String organizerId, String name, String description,
                 String categoryId, double fee, String date, String time) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.fee = fee;
        this.date = date;
        this.time = time;
    }


    //getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
