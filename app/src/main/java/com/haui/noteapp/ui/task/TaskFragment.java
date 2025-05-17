package com.haui.noteapp.ui.task;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentTaskBinding;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Priority;
import com.haui.noteapp.model.Task;
import com.haui.noteapp.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class TaskFragment extends Fragment {

    private FragmentTaskBinding binding;
    private TaskViewModel taskViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        initObserve();

        binding.fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        return binding.getRoot();
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        AutoCompleteTextView inputCategory = dialogView.findViewById(R.id.input_task_category);
        TextInputEditText inputTitle = dialogView.findViewById(R.id.input_task_title);
        TextInputEditText inputDueDate = dialogView.findViewById(R.id.input_task_due_date);

        MaterialCardView cardHigh = dialogView.findViewById(R.id.card_priority_high);
        MaterialCardView cardMedium = dialogView.findViewById(R.id.card_priority_medium);
        MaterialCardView cardLow = dialogView.findViewById(R.id.card_priority_low);

        TextView textHigh = (TextView) cardHigh.getChildAt(0);
        TextView textMedium = (TextView) cardMedium.getChildAt(0);
        TextView textLow = (TextView) cardLow.getChildAt(0);

        final String[] selectedPriority = {null};
        View.OnClickListener priorityClickListener = v -> {
            if (v == cardHigh) selectedPriority[0] = "Cao";
            else if (v == cardMedium) selectedPriority[0] = "Trung bình";
            else selectedPriority[0] = "Thấp";

            setPriorityUI(
                    cardHigh, textHigh,
                    cardMedium, textMedium,
                    cardLow, textLow,
                    selectedPriority[0]
            );
        };

        cardHigh.setOnClickListener(priorityClickListener);
        cardMedium.setOnClickListener(priorityClickListener);
        cardLow.setOnClickListener(priorityClickListener);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày đến hạn")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        inputDueDate.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            inputDueDate.setText(formattedDate);
        });

        List<Category> categories = taskViewModel.getCategoryList().getValue();
        if (categories != null && !categories.isEmpty()) {
            List<String> categoryNames = new ArrayList<>();
            Map<String, String> nameToId = new HashMap<>();

            for (Category c : categories) {
                categoryNames.add(c.getName());
                nameToId.put(c.getName(), c.getId());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, categoryNames);
            inputCategory.setAdapter(adapter);
            inputCategory.setThreshold(0);

            inputCategory.setOnClickListener(v -> inputCategory.showDropDown());
            inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) inputCategory.showDropDown();
            });


            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                    .setTitle("Thêm công việc mới")
                    .setView(dialogView)
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            if (categories != null && !categories.isEmpty()) {
                builder.setPositiveButton("Lưu", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String title = inputTitle.getText() != null ? inputTitle.getText().toString().trim() : "";
                    String categoryName = inputCategory.getText() != null ? inputCategory.getText().toString().trim() : "";
                    String dueDateStr = inputDueDate.getText() != null ? inputDueDate.getText().toString().trim() : "";

                    if (title.isEmpty()) {
                        Toast.makeText(requireContext(), "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (categoryName.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (dueDateStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn ngày đến hạn", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!DateUtil.isValidDate(dueDateStr)) {
                        Toast.makeText(requireContext(), "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (selectedPriority[0] == null) {
                        Toast.makeText(requireContext(), "Vui lòng chọn mức độ ưu tiên", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Date dueDate = DateUtil.parseDate(dueDateStr);

                    Task newTask = new Task();
                    newTask.setName(title);
                    newTask.setCategoryId(nameToId.get(categoryName));
                    newTask.setDouDate(dueDate);
                    Priority priorityEnum = Priority.fromLabel(selectedPriority[0]);
                    newTask.setPriority(priorityEnum.name());
                    newTask.setUpdatedAt(new Date());
                    newTask.setCreatedAt(new Date());

                    taskViewModel.addTask(newTask);
                    dialog.dismiss();
                });

            } else {
                builder.setPositiveButton("Hủy", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }

        }
    }

    private void setPriorityUI(
            MaterialCardView cardHigh, TextView textHigh,
            MaterialCardView cardMedium, TextView textMedium,
            MaterialCardView cardLow, TextView textLow,
            String selectedPriority
    ) {
        // Reset all
        cardHigh.setChecked(false);
        cardMedium.setChecked(false);
        cardLow.setChecked(false);

        cardHigh.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        cardMedium.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        cardLow.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));

        textHigh.setTextColor(ContextCompat.getColor(requireContext(), R.color.high_priority));
        textMedium.setTextColor(ContextCompat.getColor(requireContext(), R.color.medium_priority));
        textLow.setTextColor(ContextCompat.getColor(requireContext(), R.color.low_priority));

        // Set selected
        switch (selectedPriority) {
            case "Cao":
                cardHigh.setChecked(true);
                cardHigh.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.high_priority));
                textHigh.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case "Trung bình":
                cardMedium.setChecked(true);
                cardMedium.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.medium_priority));
                textMedium.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
            case "Thấp":
            default:
                cardLow.setChecked(true);
                cardLow.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.low_priority));
                textLow.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
                break;
        }
    }

    private void initObserve() {
        taskViewModel.getCombinedData().observe(getViewLifecycleOwner(), pair -> {
            List<Task> tasks = pair.first;
            List<Category> categories = pair.second;

            binding.recyclerTask.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);

            Map<String, Category> categoryMap = new HashMap<>();
            for (Category c : categories) categoryMap.put(c.getId(), c);

            TaskAdapter adapter = new TaskAdapter(tasks, categoryMap);

            binding.recyclerTask.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerTask.setAdapter(adapter);

            binding.recyclerTask.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        });

        taskViewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        taskViewModel.getActionMessage().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
