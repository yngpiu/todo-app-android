package com.haui.noteapp.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.haui.noteapp.databinding.FragmentCategoryBinding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haui.noteapp.databinding.FragmentCategoryBinding;

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

        viewModel.loadCategories();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
