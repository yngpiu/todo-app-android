package com.haui.noteapp.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haui.noteapp.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");

    public void getCategories(DataCallback<List<Category>> callback) {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    if (category != null) {
                        category.setId(child.getKey());
                        categories.add(category);
                    }
                }
                callback.onSuccess(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(Exception e);
    }
}
