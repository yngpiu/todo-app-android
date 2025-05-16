package com.haui.noteapp.repository;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;

public class UserRepository {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    public void signUp(String email, String password, User user, IFirebaseCallbackListener<Void> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        user.setId(uid);
                        user.setEmail(email);
                        userRef.child(uid).setValue(user)
                                .addOnSuccessListener(unused -> listener.onFirebaseLoadSuccess(null))
                                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
                    } else {
                        listener.onFirebaseLoadFailed("FirebaseUser is null");
                    }
                })
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }
}
