package com.haui.noteapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;

import android.util.Log;

public class UserRepository {

    private static final String TAG = "UserRepository";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void signUp(String email, String password, User user, IFirebaseCallbackListener<Void> callback) {
        Log.d(TAG, "Đang đăng ký user với email: " + email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        Log.d(TAG, "Đăng ký thất bại: Không thể lấy thông tin người dùng.");
                        callback.onFirebaseLoadFailed("Đăng ký thất bại: Không thể lấy thông tin người dùng.");
                        return;
                    }

                    String uid = firebaseUser.getUid();
                    user.setEmail(email);

                    Log.d(TAG, "Lưu thông tin user vào Firestore với id: " + uid);
                    db.collection("users").document(uid).set(user)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "Đăng ký user thành công id=" + uid);
                                callback.onFirebaseLoadSuccess(null);
                            })
                            .addOnFailureListener(e -> {
                                Log.d(TAG, "Lưu user thất bại: " + e.getMessage());
                                callback.onFirebaseLoadFailed(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Đăng ký thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }

    public void login(String email, String password, IFirebaseCallbackListener<FirebaseUser> callback) {
        Log.d(TAG, "Đang đăng nhập user với email: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        Log.d(TAG, "Đăng nhập thất bại: Không thể lấy thông tin người dùng.");
                        callback.onFirebaseLoadFailed("Đăng nhập thất bại: Không thể lấy thông tin người dùng.");
                    } else {
                        Log.d(TAG, "Đăng nhập thành công user id=" + firebaseUser.getUid());
                        callback.onFirebaseLoadSuccess(firebaseUser);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Đăng nhập thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }

    public void signOut() {
        Log.d(TAG, "Đăng xuất user");
        mAuth.signOut();
    }
}
