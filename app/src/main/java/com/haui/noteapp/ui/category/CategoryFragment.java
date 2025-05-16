package com.haui.noteapp.ui.category;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentCategoryBinding;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.haui.noteapp.model.Category;
import com.haui.noteapp.util.ColorUtil;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        adapter = new CategoryAdapter();
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewCategories.setAdapter(adapter);

        viewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategoryList(categories);
        });
        binding.fabAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        viewModel.loadCategories();

        return binding.getRoot();
    }


    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm danh mục mới");

        View dialogView = LayoutInflater.from(getContext()).inflate(com.haui.noteapp.R.layout.dialog_add_category, null);
        TextInputEditText inputCategoryName = dialogView.findViewById(R.id.inputCategoryName);

        builder.setView(dialogView);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = inputCategoryName.getText() != null ? inputCategoryName.getText().toString().trim() : "";
            if (!name.isEmpty()) {
                String randomColor = ColorUtil.getRandomColor();
                addCategory(name, randomColor);
            }
        });
        builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addCategory(String name, String color) {
        // Tạo category mới
        Category newCategory = new Category();
        newCategory.setName(name);
        newCategory.setColorHex(color);

        viewModel.addCategory(newCategory);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
