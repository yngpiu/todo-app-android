package com.haui.noteapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class CategoryRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final String TAG = "CategoryRepository";

    public void loadData(IFirebaseCallbackListener<List<Category>> callback) {
        String userId = getUserId(callback);
        if (userId == null) {
            Log.d(TAG, "loadData: Người dùng chưa đăng nhập");
            return;
        }

        Log.d(TAG, "loadData: Đang tải danh mục cho userId=" + userId);
        db.collection("categories")
                .whereEqualTo("userId", userId)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d(TAG, "loadData: Lỗi tải dữ liệu: " + error.getMessage());
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (value == null) {
                        Log.d(TAG, "loadData: Không có dữ liệu");
                        callback.onFirebaseLoadFailed("Dữ liệu không tồn tại");
                        return;
                    }

                    List<Category> categories = new ArrayList<>();
                    for (var doc : value) {
                        Category category = doc.toObject(Category.class);
                        category.setId(doc.getId());
                        categories.add(category);
                    }
                    Log.d(TAG, "loadData: Đã tải " + categories.size() + " danh mục");
                    callback.onFirebaseLoadSuccess(categories);
                });
    }

    public void addCategory(Category category, IFirebaseCallbackListener<Void> callback) {
        String userId = getUserId(callback);
        if (userId == null) {
            Log.d(TAG, "addCategory: Người dùng chưa đăng nhập");
            return;
        }

        category.setUserId(userId);
        db.collection("categories")
                .add(category)
                .addOnSuccessListener(docRef -> {
                    category.setId(docRef.getId());
                    Log.d(TAG, "addCategory: Thêm danh mục thành công id=" + docRef.getId());
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "addCategory: Thêm danh mục thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }

    public void updateCategory(Category category, IFirebaseCallbackListener<Void> callback) {
        Log.d(TAG, "updateCategory: Cập nhật danh mục id=" + category.getId());
        db.collection("categories").document(category.getId())
                .set(category)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "updateCategory: Cập nhật thành công id=" + category.getId());
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "updateCategory: Cập nhật thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }

    public void deleteCategory(String categoryId, IFirebaseCallbackListener<Void> callback) {
        Log.d(TAG, "deleteCategory: Xóa danh mục id=" + categoryId);
        db.collection("categories").document(categoryId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "deleteCategory: Xóa thành công id=" + categoryId);
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "deleteCategory: Xóa thất bại: " + e.getMessage());
                    callback.onFirebaseLoadFailed(e.getMessage());
                });
    }

    private String getUserId(IFirebaseCallbackListener<?> callback) {
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "getUserId: Người dùng chưa đăng nhập");
            callback.onFirebaseLoadFailed("Vui lòng đăng nhập.");
            return null;
        }
        return mAuth.getCurrentUser().getUid();
    }
}
