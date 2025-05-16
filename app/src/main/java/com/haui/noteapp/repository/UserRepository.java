package com.haui.noteapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;

public class UserRepository {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void signUp(String email, String password, User user, IFirebaseCallbackListener<Void> callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFirebaseLoadFailed("Đăng ký thất bại: Không thể lấy thông tin người dùng.");
                        return;
                    }

                    String uid = firebaseUser.getUid();
                    user.setId(uid);
                    user.setEmail(email);

                    db.collection("users").document(uid).set(user)
                            .addOnSuccessListener(unused -> callback.onFirebaseLoadSuccess(null))
                            .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
    }

    public void login(String email, String password, IFirebaseCallbackListener<FirebaseUser> callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        callback.onFirebaseLoadFailed("Đăng nhập thất bại: Không thể lấy thông tin người dùng.");
                    } else {
                        callback.onFirebaseLoadSuccess(firebaseUser);
                    }
                })
                .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
    }

    public void signOut() {
        mAuth.signOut();
    }

}
