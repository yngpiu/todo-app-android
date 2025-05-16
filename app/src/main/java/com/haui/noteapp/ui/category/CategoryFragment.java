package com.haui.noteapp.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haui.noteapp.databinding.FragmentCategoryBinding;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        initBinding();
        initViewModel();
        initObserve();

        categoryViewModel.loadData();

        return binding.getRoot();
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
            CategoryAdapter adapter = new CategoryAdapter(categories);
            binding.recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerCategory.setAdapter(adapter);
            binding.recyclerCategory.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
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
