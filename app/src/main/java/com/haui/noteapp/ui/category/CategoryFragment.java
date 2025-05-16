package com.haui.noteapp.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentCategoryBinding;
import com.haui.noteapp.listener.OnCategoryActionListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.util.ColorUtil;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        initBinding();
        initViewModel();
        initObserve();

        binding.fabAddTask.setOnClickListener(v -> showAddCategoryDialog());

        return binding.getRoot();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm danh mục");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_category, null);
        TextInputEditText input = view.findViewById(R.id.input_category_name);
        builder.setView(view);

        builder.setPositiveButton("Thêm", ((dialog, which) ->
        {
            String name = "";
            if (input.getText() != null) {
                name = input.getText().toString().trim();
            }
            Category category = new Category();
            category.setName(name);
            category.setColorHex(ColorUtil.getRandomColor());
            categoryViewModel.addCategory(category);

        }));
        builder.setNegativeButton("Hủy", ((dialog, which) -> dialog.dismiss()));
        builder.show();
    }

    private void initBinding() {
        binding = FragmentCategoryBinding.inflate(getLayoutInflater());
    }

    private void initObserve() {

        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            binding.recyclerCategory.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            CategoryAdapter adapter = new CategoryAdapter(categories, new OnCategoryActionListener() {
                @Override
                public void onUpdate(Category category) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Chỉnh sửa danh mục");

                    View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_category, null);
                    TextInputEditText input = view.findViewById(R.id.input_category_name);
                    input.setText(category.getName());

                    builder.setView(view);
                    builder.setPositiveButton("Lưu", (dialog, which) -> {
                        String newName = "";
                        if (input.getText() != null) {
                            newName = input.getText().toString().trim();
                        }
                        if (newName.isEmpty()) {
                            Toast.makeText(getContext(), "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newName.equals(category.getName())) {
                            category.setName(newName);
                            categoryViewModel.updateCategory(category);
                        }
                    });

                    builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss());
                    builder.show();
                }

                @Override
                public void onDelete(Category category) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Xoá danh mục")
                            .setMessage("Bạn có chắc muốn xoá danh mục [" + category.getName() + "] không?")
                            .setPositiveButton("Xoá", ((dialog, which) ->
                                    categoryViewModel.deleteCategory(category.getId()))).setNegativeButton("Huỷ", null)
                            .show();
                }
            });
            binding.recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerCategory.setAdapter(adapter);
            binding.recyclerCategory.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });
        categoryViewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        categoryViewModel.getActionMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViewModel() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
