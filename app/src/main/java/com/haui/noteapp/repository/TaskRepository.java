package com.haui.noteapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadData(IFirebaseCallbackListener<List<Task>> callback) {

        if (mAuth.getCurrentUser() == null) {
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (value == null) {
                        callback.onFirebaseLoadFailed("Dữ liệu không tồn tại");
                        return;
                    }

                    List<Task> tasks = new ArrayList<>();
                    for (var doc : value) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        tasks.add(task);
                    }
                    callback.onFirebaseLoadSuccess(tasks);
                });
    }

    public void addTask(Task task, IFirebaseCallbackListener<Void> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;
        task.setUserId(userId);
        db.collection("tasks").add(task)
                .addOnSuccessListener(ref -> {
                    task.setId(ref.getId());
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
    }

    private String getUserId(IFirebaseCallbackListener<?> callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onFirebaseLoadFailed("Vui lòng đăng nhập.");
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }

}
