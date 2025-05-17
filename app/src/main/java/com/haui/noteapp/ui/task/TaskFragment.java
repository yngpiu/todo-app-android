package com.haui.noteapp.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentTaskBinding;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Task;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        binding.fabAddTask.setOnClickListener(v -> {
            showAddTaskDialog();
        });

        return binding.getRoot();
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        AutoCompleteTextView inputCategory = dialogView.findViewById(R.id.input_task_category);
        TextInputEditText inputTitle = dialogView.findViewById(R.id.input_task_title);
        TextInputEditText inputDueDate = dialogView.findViewById(R.id.input_task_due_date);
        MaterialCardView cardPriorityHigh = dialogView.findViewById(R.id.card_priority_high);
        MaterialCardView cardPriorityMedium = dialogView.findViewById(R.id.card_priority_medium);
        MaterialCardView cardPriorityLow = dialogView.findViewById(R.id.card_priority_low);

        final String[] priority = {"Thấp"};
        cardPriorityHigh.setOnClickListener(v -> {
            priority[0] = "Cao";
            cardPriorityHigh.setChecked(true);
            cardPriorityMedium.setChecked(false);
            cardPriorityLow.setChecked(false);
        });
        cardPriorityMedium.setOnClickListener(v -> {
            priority[0] = "Trung bình";
            cardPriorityHigh.setChecked(false);
            cardPriorityMedium.setChecked(true);
            cardPriorityLow.setChecked(false);
        });
        cardPriorityLow.setOnClickListener(v -> {
            priority[0] = "Thấp";
            cardPriorityHigh.setChecked(false);
            cardPriorityMedium.setChecked(false);
            cardPriorityLow.setChecked(true);
        });

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày đến hạn")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        inputDueDate.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            inputDueDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection)));
        });

        List<Category> categories = taskViewModel.getCategoryList().getValue();
        List<String> categoryNames = new ArrayList<>();
        Map<String, String> categoryNameToIdMap = new HashMap<>();

        if (categories != null && !categories.isEmpty()) {
            for (Category c : categories) {
                categoryNames.add(c.getName());
                categoryNameToIdMap.put(c.getName(), c.getId());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    categoryNames
            );
            inputCategory.setAdapter(adapter);
            inputCategory.setThreshold(0);

            inputCategory.setOnClickListener(v -> {
                inputCategory.showDropDown();
            });

            inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    inputCategory.showDropDown();
                }
            });
        } else {
            inputCategory.setEnabled(false);
            inputCategory.setHint("Chưa có danh mục");
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Thêm công việc mới")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String title = inputTitle.getText().toString().trim();
                    String selectedCategoryName = inputCategory.getText().toString().trim();
                    String dueDateStr = inputDueDate.getText().toString().trim();

                    if (title.isEmpty()) {
                        inputTitle.setError("Tiêu đề không được để trống");
                        return;
                    }
                    if (selectedCategoryName.isEmpty()) {
                        inputCategory.setError("Vui lòng chọn danh mục");
                        return;
                    }
                    if (dueDateStr.isEmpty()) {
                        inputDueDate.setError("Vui lòng chọn ngày đến hạn");
                        return;
                    }

                    Date dueDate;
                    try {
                        dueDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dueDateStr);
                    } catch (Exception e) {
                        inputDueDate.setError("Ngày không hợp lệ");
                        return;
                    }

                    Task newTask = new Task();
                    newTask.setName(title);
                    newTask.setCategoryId(categoryNameToIdMap.get(selectedCategoryName));
                    newTask.setDouDate(dueDate);
                    newTask.setPriority(priority[0]);
                    newTask.setUpdatedAt(new Date());

                    taskViewModel.addTask(newTask);

                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
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