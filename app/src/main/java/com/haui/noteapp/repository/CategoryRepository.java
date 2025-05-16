package com.haui.noteapp.repository;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final IFirebaseCallbackListener<List<Category>> iFirebaseCallbackListener;
    private final DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CategoryRepository(IFirebaseCallbackListener<List<Category>> iFirebaseCallbackListener) {
        this.iFirebaseCallbackListener = iFirebaseCallbackListener;
    }

    public void loadData() {
        List<Category> categories = new ArrayList<>();
        categoryRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Category model = itemSnapshot.getValue(Category.class);
                    categories.add(model);
                }
                iFirebaseCallbackListener.onFirebaseLoadSuccess(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iFirebaseCallbackListener.onFirebaseLoadFailed(error.getMessage());
            }
        });
    }

    public void addCategory(Category category, IFirebaseCallbackListener<Void> listener) {
        category.setUserId(mAuth.getCurrentUser().getUid());
        String key = categoryRef.push().getKey();
        if (key == null) {
            listener.onFirebaseLoadFailed("Không thể thêm danh mục");
            return;
        }
        category.setId(key);
        categoryRef.child(key).setValue(category).addOnSuccessListener(unused -> {
            listener.onFirebaseLoadSuccess(null);
        }).addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

    public void deleteCategory(String categoryId, IFirebaseCallbackListener<Void> listener) {
        categoryRef.child(categoryId)
                .removeValue()
                .addOnSuccessListener(unused -> listener.onFirebaseLoadSuccess(null))
                .addOnFailureListener(e -> listener.onFirebaseLoadFailed(e.getMessage()));
    }

}
