package com.haui.noteapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticRepository {

    private static final String TAG = "StatisticRepository";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final CategoryRepository categoryRepository = new CategoryRepository();

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

    public void getTaskPriorityStats(IFirebaseCallbackListener<Map<String, Integer>> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.d(TAG, "Lỗi khi lấy thống kê ưu tiên: " + error.getMessage());
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (snapshot != null) {
                        Map<String, Integer> priorityStats = new HashMap<>();
                        priorityStats.put("HIGH", 0);
                        priorityStats.put("MEDIUM", 0);
                        priorityStats.put("LOW", 0);

                        for (var doc : snapshot) {
                            Task task = doc.toObject(Task.class);
                            String priority = task.getPriority();
                            priorityStats.put(priority, priorityStats.getOrDefault(priority, 0) + 1);
                        }

                        callback.onFirebaseLoadSuccess(priorityStats);
                    }
                });
    }

    public void getTaskCategoryStats(IFirebaseCallbackListener<Map<String, Map<String, Integer>>> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        // Tải danh sách danh mục trước
        categoryRepository.loadData(new IFirebaseCallbackListener<List<Category>>() {
            @Override
            public void onFirebaseLoadSuccess(List<Category> categories) {
                Map<String, String> categoryMap = new HashMap<>();
                for (Category category : categories) {
                    categoryMap.put(category.getId(), category.getName());
                }

                // Tải thống kê từ tasks
                db.collection("tasks")
                        .whereEqualTo("userId", userId)
                        .addSnapshotListener((snapshot, error) -> {
                            if (error != null) {
                                Log.d(TAG, "Lỗi khi lấy thống kê danh mục: " + error.getMessage());
                                callback.onFirebaseLoadFailed(error.getMessage());
                                return;
                            }

                            if (snapshot != null) {
                                Map<String, Map<String, Integer>> categoryStats = new HashMap<>();

                                for (var doc : snapshot) {
                                    Task task = doc.toObject(Task.class);
                                    String categoryId = task.getCategoryId();
                                    Boolean isCompleted = task.isCompleted();

                                    String categoryName = categoryMap.getOrDefault(categoryId, "Không xác định");
                                    categoryStats.putIfAbsent(categoryName, new HashMap<>());
                                    Map<String, Integer> statusStats = categoryStats.get(categoryName);
                                    statusStats.putIfAbsent("completed", 0);
                                    statusStats.putIfAbsent("pending", 0);

                                    if (Boolean.TRUE.equals(isCompleted)) {
                                        statusStats.put("completed", statusStats.get("completed") + 1);
                                    } else {
                                        statusStats.put("pending", statusStats.get("pending") + 1);
                                    }
                                }

                                callback.onFirebaseLoadSuccess(categoryStats);
                            }
                        });
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                Log.d(TAG, "Lỗi khi tải danh mục: " + message);
                callback.onFirebaseLoadFailed(message);
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