package com.haui.noteapp.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final IFirebaseCallbackListener<List<Category>> iFirebaseCallbackListener;
    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CategoryRepository(IFirebaseCallbackListener<List<Category>> iFirebaseCallbackListener) {
        this.iFirebaseCallbackListener = iFirebaseCallbackListener;
        db = FirebaseFirestore.getInstance();
        loadData();
    }

    public void loadData() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        db.collection("categories")
                .whereEqualTo("userId", userId)
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        iFirebaseCallbackListener.onFirebaseLoadFailed(error.getMessage());
                        Log.d(
                                "CategoryRepository",
                                "Error getting documents: " + error.getMessage()
                        );
                        return;
                    }

                    List<Category> categories = new ArrayList<>();
                    for (var doc : value) {
                        Category category = doc.toObject(Category.class);
                        category.setId(doc.getId());
                        categories.add(category);
                    }

                    iFirebaseCallbackListener.onFirebaseLoadSuccess(categories);
                });
    }


    public void addCategory(Category category, IFirebaseCallbackListener<Void> listener) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        category.setUserId(userId);
        db.collection("categories").add(category)
                .addOnSuccessListener(documentReference -> {
                    category.setId(documentReference.getId());
                    listener.onFirebaseLoadSuccess(null);
                }).addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

    public void updateCategory(Category category, IFirebaseCallbackListener<Void> listener) {
        db.collection("categories")
                .document(category.getId()).set(category)
                .addOnSuccessListener(unused -> listener.onFirebaseLoadSuccess(null))
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

    public void deleteCategory(String categoryId, IFirebaseCallbackListener<Void> listener) {
        db.collection("categories")
                .document(categoryId).delete()
                .addOnSuccessListener(unused -> listener.onFirebaseLoadSuccess(null))
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }
}