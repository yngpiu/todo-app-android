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
import com.google.firebase.auth.FirebaseAuth;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentCategoryBinding;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.util.ColorUtil;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        initBinding();
        initViewModel();
        initObserve();

        categoryViewModel.loadData();
        binding.fabAddTask.setOnClickListener(v -> showAddCategoryDialog());

        return binding.getRoot();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm danh mục");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_category, null);
        TextInputEditText input = view.findViewById(R.id.input_category_name);
        builder.setView(view);

        builder.setPositiveButton("Thêm", ((dialog, which) ->
        {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
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
        categoryViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        });
        categoryViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            binding.recyclerCategory.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xoá danh mục")
                        .setMessage("Bạn có chắc muốn xoá danh mục [" + category.getName() + "] không?")
                        .setPositiveButton("Xoá", ((dialog, which) ->
                        {
                            categoryViewModel.deleteCategory(category.getId());
                        })).setNegativeButton("Huỷ", null)
                        .show();
            });
            binding.recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerCategory.setAdapter(adapter);
            binding.recyclerCategory.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });
        categoryViewModel.getAddSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewModel() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
