package com.haui.noteapp.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haui.noteapp.model.User;

public class UserRepository {
    private final DatabaseReference mDatabase;

    public  UserRepository() {
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    public void createUser(User user) {
        mDatabase.child(user.getId()).setValue(user);
    }
}
