package com.haui.noteapp.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void loadData(IFirebaseCallbackListener<List<Category>> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        db.collection("categories")
                .whereEqualTo("userId", userId)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onFirebaseLoadFailed(error.getMessage());
                        return;
                    }

                    if (value == null) {
                        callback.onFirebaseLoadFailed("Dữ liệu không tồn tại");
                        return;
                    }

                    List<Category> categories = new ArrayList<>();
                    for (var doc : value) {
                        Category category = doc.toObject(Category.class);
                        category.setId(doc.getId());
                        categories.add(category);
                    }
                    callback.onFirebaseLoadSuccess(categories);
                });
    }

    public void addCategory(Category category, IFirebaseCallbackListener<Void> callback) {
        String userId = getUserId(callback);
        if (userId == null) return;

        category.setUserId(userId);
        db.collection("categories").add(category)
                .addOnSuccessListener(ref -> {
                    category.setId(ref.getId());
                    callback.onFirebaseLoadSuccess(null);
                })
                .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
    }

    public void updateCategory(Category category, IFirebaseCallbackListener<Void> callback) {
        db.collection("categories").document(category.getId())
                .set(category)
                .addOnSuccessListener(unused -> callback.onFirebaseLoadSuccess(null))
                .addOnFailureListener(e -> callback.onFirebaseLoadFailed(e.getMessage()));
    }

    public void deleteCategory(String categoryId, IFirebaseCallbackListener<Void> callback) {
        db.collection("categories").document(categoryId)
                .delete()
                .addOnSuccessListener(unused -> callback.onFirebaseLoadSuccess(null))
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
