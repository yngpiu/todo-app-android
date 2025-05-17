package com.haui.noteapp.model;


import com.google.firebase.firestore.Exclude;

public class Category {
    @Exclude
    private String id;
    private String name;
    private String colorHex;
    private String userId;

    public Category() {

    }

    public Category(String id, String name, String colorHex, String userId) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
        this.userId = userId;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
