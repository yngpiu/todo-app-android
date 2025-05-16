package com.haui.noteapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;

public class UserRepository {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public void signUp(String email, String password, User user, IFirebaseCallbackListener<Void> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        user.setId(uid);
                        user.setEmail(email);
                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(unused -> listener.onFirebaseLoadSuccess(null))
                                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
                    } else {
                        listener.onFirebaseLoadFailed("FirebaseUser is null");
                    }
                })
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

    public void login(String email, String password, IFirebaseCallbackListener<FirebaseUser> listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        listener.onFirebaseLoadSuccess(firebaseUser);
                    } else {
                        listener.onFirebaseLoadFailed("FirebaseUser is null");
                    }
                })
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

    public void signOut() {
        mAuth.signOut();
    }
}