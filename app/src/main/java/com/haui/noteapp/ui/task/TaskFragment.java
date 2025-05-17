package com.haui.noteapp.ui.task;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haui.noteapp.databinding.FragmentTaskBinding;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFragment extends Fragment {

    private FragmentTaskBinding binding;
    private TaskViewModel taskViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        initBinding();
        initViewModel();
        initObserve();

        return binding.getRoot();
    }

    private void initBinding() {
        binding = FragmentTaskBinding.inflate(getLayoutInflater());
    }

    private void initObserve() {
        taskViewModel.getCombinedData().observe(getViewLifecycleOwner(), pair -> {
            List<Task> tasks = pair.first;
            List<Category> categories = pair.second;

            binding.recyclerTask.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);

            Map<String, Category> categoryMap = new HashMap<>();
            for (Category c : categories) {
                categoryMap.put(c.getId(), c);
            }

            TaskAdapter adapter = new TaskAdapter(tasks, categoryMap);

            binding.recyclerTask.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerTask.setAdapter(adapter);

            binding.recyclerTask.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });

        taskViewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        taskViewModel.getActionMessage().observe(getViewLifecycleOwner(), event -> {
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void initViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
