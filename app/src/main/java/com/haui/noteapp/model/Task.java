package com.haui.noteapp.model;

import java.util.Date;

public class Task {
    private String id;
    private String name;
    private Date douDate;
    private String categoryId;
    private Date createdAt;
    private Date updatedAt;
    private String priority;

    public Task() {
    }

    public Task(String id, String name, Date douDate, String categoryId, Date createdAt, Date updatedAt, String priority) {
        this.id = id;
        this.name = name;
        this.douDate = douDate;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
    }

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

    public Date getDouDate() {
        return douDate;
    }

    public void setDouDate(Date douDate) {
        this.douDate = douDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
}
