package com.haui.noteapp.model;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Task {
    @Exclude
    private String id;
    private String name;
    private Date dueDate;
    private String categoryId;
    private String userId;
    private Date createdAt;
    private Date updatedAt;
    private String priority;
    private boolean isCompleted;

    public Task() {
    }

    public Task(String id, String name, Date dueDate, String categoryId, String userId, Date createdAt, Date updatedAt, String priority, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.categoryId = categoryId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
        this.isCompleted = isCompleted;

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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
