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

    private static final String TAG = "TaskRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadData(IFirebaseCallbackListener<List<Task>> callback) {
        Log.d(TAG, "Bắt đầu tải danh sách task");

        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "Chưa đăng nhập, không thể tải dữ liệu task");
            callback.onFirebaseLoadFailed("Vui lòng đăng nhập.");
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d(TAG, "Lỗi khi tải dữ liệu task: " + error.getMessage());
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (value == null) {
                        Log.d(TAG, "Dữ liệu task trả về null");
                        callback.onFirebaseLoadFailed("Dữ liệu không tồn tại");
                        return;
                    }

                    List<Task> tasks = new ArrayList<>();
                    for (var doc : value) {
                        Task task = doc.toObject(Task.class);
                        task.setId(doc.getId());
                        tasks.add(task);
                    }
                    Log.d(TAG, "Tải dữ liệu task thành công, tổng: " + tasks.size());
                    callback.onFirebaseLoadSuccess(tasks);
                });
    }

    public void addTask(Task task, IFirebaseCallbackListener<Void> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        task.setUserId(userId);

        Log.d(TAG, "Thêm task mới cho userId: " + userId);
        db.collection("tasks").add(task)
                .addOnSuccessListener(ref -> {
                    Log.d(TAG, "Thêm task thành công với id: " + ref.getId());
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Thêm task thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }


    public void deleteTask(String taskId, IFirebaseCallbackListener<Void> callback) {
        if (taskId == null) {
            Log.d(TAG, "Task ID is null, cannot delete");
            callback.onFirebaseLoadFailed("Task ID không hợp lệ");
            return;
        }

        String userId = getUserId(callback);
        if (userId == null) return;

        Log.d(TAG, "Xóa task với id: " + taskId);
        db.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Xóa task thành công");
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Xóa task thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }
    public void updateTask(Task task, IFirebaseCallbackListener<Void> callback) {
        String userId = getUserId(callback);
        if (userId == null || task.getId() == null) return;

        task.setUserId(userId);
        db.collection("tasks")
                .document(task.getId())
                .set(task)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Cập nhật task thành công");
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Cập nhật task thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
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
