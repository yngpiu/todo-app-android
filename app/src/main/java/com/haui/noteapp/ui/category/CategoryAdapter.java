package com.haui.noteapp.ui.category;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.noteapp.databinding.ItemCategoryBinding;
import com.haui.noteapp.listener.OnCategoryActionListener;
import com.haui.noteapp.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> categories;
    private final OnCategoryActionListener listener;

    public CategoryAdapter(List<Category> categories, OnCategoryActionListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.binding.categoryName.setText(category.getName());
        holder.binding.colorDot.setColorFilter(android.graphics.Color.parseColor(category.getColorHex()));
        holder.binding.deleteButton.setOnClickListener(v -> listener.onDelete(category));
        holder.binding.editButton.setOnClickListener(v -> listener.onUpdate(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
