package com.haui.noteapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.model.Task;

public class StatisticRepository {

    private static final String TAG = "StatisticRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void getTaskStats(IFirebaseCallbackListener<StatisticData> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.d(TAG, "Lỗi khi thống kê realtime: " + error.getMessage());
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (snapshot != null) {
                        int total = snapshot.size();
                        int completed = 0;

                        for (var doc : snapshot) {
                            Task task = doc.toObject(Task.class);
                            if (Boolean.TRUE.equals(task.isCompleted())) {
                                completed++;
                            }
                        }

                        int pending = total - completed;
                        int rate = total == 0 ? 0 : (completed * 100 / total);

                        callback.onFirebaseLoadSuccess(new StatisticData(total, completed, pending, rate));
                    }
                });
    }


    private String getUserId(IFirebaseCallbackListener<?> callback) {
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "Người dùng chưa đăng nhập");
            callback.onFirebaseLoadFailed("Vui lòng đăng nhập.");
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }
}
