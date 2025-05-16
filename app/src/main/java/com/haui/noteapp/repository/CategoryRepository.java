package com.haui.noteapp.repository;


import androidx.annotation.NonNull;

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

    public CategoryRepository(IFirebaseCallbackListener<List<Category>> iFirebaseCallbackListener) {
        this.iFirebaseCallbackListener = iFirebaseCallbackListener;
    }

    public  void loadData() {
        List<Category> categories = new ArrayList<>();
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot : snapshot.getChildren()) {
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
}
