package com.haui.noteapp.ui.task;

import android.app.AlertDialog;
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

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.haui.noteapp.R;
import com.haui.noteapp.databinding.FragmentTaskBinding;
import com.haui.noteapp.listener.OnTaskActionListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Priority;
import com.haui.noteapp.model.Task;
import com.haui.noteapp.util.DateUtil;
import com.haui.noteapp.util.PrioritySelector;

import java.text.SimpleDateFormat;
import java.util.*;

public class TaskFragment extends Fragment implements OnTaskActionListener {

    private FragmentTaskBinding binding;
    private TaskViewModel taskViewModel;
    private AutoCompleteTextView filterCategory;
    private AutoCompleteTextView filterPriority;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        initUI();
        setupObservers();

        return binding.getRoot();
    }

    private void initUI() {
        binding.fabAddTask.setOnClickListener(v -> onAddTaskClicked());
        setupRecyclerView();

        // Initialize filter views
        filterCategory = binding.filterCategory;
        filterPriority = binding.filterPriority;

        // Set up filters
        setupCategoryFilter();
        setupPriorityFilter();
    }

    private void setupCategoryFilter() {
        taskViewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                List<String> categoryNames = new ArrayList<>();
                categoryNames.add("Tất cả"); // Add "All" option
                for (Category category : categories) {
                    categoryNames.add(category.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                filterCategory.setAdapter(adapter);
                filterCategory.setText("Tất cả", false); // Default to "All"
                filterCategory.setOnItemClickListener((parent, view, position, id) -> applyFilters());
            } else {
                filterCategory.setText("Tất cả", false); // Default to "All" if no categories
            }
        });
    }

    private void setupPriorityFilter() {
        List<String> priorityLabels = new ArrayList<>();
        priorityLabels.add("Tất cả"); // Add "All" option
        priorityLabels.add(Priority.HIGH.getLabel());    // "Cao"
        priorityLabels.add(Priority.MEDIUM.getLabel());  // "Trung bình"
        priorityLabels.add(Priority.LOW.getLabel());     // "Thấp"
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, priorityLabels);
        filterPriority.setAdapter(adapter);
        filterPriority.setText("Tất cả", false); // Default to "All"
        filterPriority.setOnItemClickListener((parent, view, position, id) -> applyFilters());
    }

    private void applyFilters() {
        String selectedCategory = filterCategory.getText().toString();
        String selectedPriority = filterPriority.getText().toString();

        taskViewModel.getCombinedData().observe(getViewLifecycleOwner(), pair -> {
            List<Task> tasks = pair.first != null ? pair.first : new ArrayList<>(); // Ensure tasks is not null
            List<Category> categories = pair.second;

            List<Task> filteredTasks = new ArrayList<>();
            for (Task task : tasks) {
                boolean categoryMatch = "Tất cả".equals(selectedCategory) ||
                        (categories != null && categories.stream().anyMatch(c -> c.getName().equals(selectedCategory) && c.getId().equals(task.getCategoryId())));
                boolean priorityMatch = "Tất cả".equals(selectedPriority) ||
                        Priority.fromLabel(selectedPriority).name().equals(task.getPriority());

                if (categoryMatch && priorityMatch) {
                    filteredTasks.add(task);
                }
            }

            updateTaskList(filteredTasks, categories);
        });
    }

    private void onAddTaskClicked() {
        List<Category> categories = taskViewModel.getCategoryList().getValue();
        if (categories == null || categories.isEmpty()) {
            Toast.makeText(requireContext(), "Chưa có danh mục nào. Vui lòng tạo danh mục trước.", Toast.LENGTH_SHORT).show();
            return;
        }
        Task newTask = new Task();
        newTask.setDueDate(new Date());
        newTask.setPriority(Priority.MEDIUM.name());
        showTaskDialog(newTask, false);
    }

    private void setupRecyclerView() {
        binding.recyclerTask.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupObservers() {
        taskViewModel.getCombinedData().observe(getViewLifecycleOwner(), pair -> {
            List<Task> tasks = pair.first != null ? pair.first : new ArrayList<>();
            List<Category> categories = pair.second != null ? pair.second : new ArrayList<>();
            // Apply filters when data changes
            applyFilters();
        });

        taskViewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        taskViewModel.getActionMessage().observe(getViewLifecycleOwner(), event -> {
            String msg = event.getContentIfNotHandled();
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                applyFilters(); // Reapply filters after action (e.g., add/update/delete)
            }
        });
    }

    private void updateTaskList(List<Task> tasks, List<Category> categories) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerTask.setVisibility(View.GONE);

        Map<String, Category> categoryMap = new HashMap<>();
        if (categories != null) {
            for (Category c : categories) categoryMap.put(c.getId(), c);
        }

        TaskAdapter adapter = new TaskAdapter(tasks, categoryMap, this);
        binding.recyclerTask.setAdapter(adapter);

        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerTask.setVisibility(View.VISIBLE);
    }

    private void showTaskDialog(Task task, boolean isEdit) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        AutoCompleteTextView inputCategory = dialogView.findViewById(R.id.input_task_category);
        TextInputEditText inputTitle = dialogView.findViewById(R.id.input_task_title);
        TextInputEditText inputDueDate = dialogView.findViewById(R.id.input_task_due_date);
        PrioritySelector prioritySelector = new PrioritySelector(dialogView);

        setupCategoryDropdown(inputCategory);
        setupDatePicker(inputDueDate, task.getDueDate());
        bindTaskToForm(task, inputTitle, inputDueDate, inputCategory, prioritySelector);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(isEdit ? "Cập nhật công việc" : "Thêm công việc mới")
                .setView(dialogView)
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .setPositiveButton("Lưu", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (handleSaveTask(task, inputTitle, inputCategory, inputDueDate, prioritySelector, isEdit)) {
                dialog.dismiss();
            }
        }));

        dialog.show();
    }

    private void setupCategoryDropdown(AutoCompleteTextView inputCategory) {
        List<Category> categories = taskViewModel.getCategoryList().getValue();
        if (categories == null || categories.isEmpty()) return;

        List<String> categoryNames = new ArrayList<>(categories.size());
        for (Category c : categories) categoryNames.add(c.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        inputCategory.setAdapter(adapter);
        inputCategory.setThreshold(0);

        inputCategory.setOnClickListener(v -> inputCategory.showDropDown());
        inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) inputCategory.showDropDown();
        });
    }

    private void setupDatePicker(TextInputEditText inputDueDate, Date initialDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày đến hạn")
                .setSelection(initialDate != null ? initialDate.getTime() : MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        inputDueDate.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            inputDueDate.setText(formattedDate);
        });
    }

    private void bindTaskToForm(Task task, TextInputEditText inputTitle, TextInputEditText inputDueDate,
                                AutoCompleteTextView inputCategory, PrioritySelector prioritySelector) {
        List<Category> categories = taskViewModel.getCategoryList().getValue();
        Map<String, String> idToName = new HashMap<>();
        if (categories != null) {
            for (Category c : categories) {
                idToName.put(c.getId(), c.getName());
            }
        }

        inputTitle.setText(task.getName());
        inputDueDate.setText(DateUtil.formatDate(task.getDueDate()));
        inputCategory.setText(idToName.getOrDefault(task.getCategoryId(), ""), false);
        prioritySelector.setSelectedPriority(Priority.fromKey(task.getPriority()).getLabel());
    }

    @Override
    public void onDelete(Task task) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa công việc '" + task.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    taskViewModel.deleteTask(task.getId());
                    showToast("Đã xóa công việc");
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onCompleteTask(Task task) {
        taskViewModel.updateTask(task);
    }

    private boolean handleSaveTask(Task task, TextInputEditText inputTitle, AutoCompleteTextView inputCategory,
                                   TextInputEditText inputDueDate, PrioritySelector prioritySelector, boolean isEdit) {
        String title = Optional.ofNullable(inputTitle.getText()).map(CharSequence::toString).orElse("").trim();
        String categoryName = Optional.ofNullable(inputCategory.getText()).map(CharSequence::toString).orElse("").trim();
        String dueDateStr = Optional.ofNullable(inputDueDate.getText()).map(CharSequence::toString).orElse("").trim();
        String selectedPriority = prioritySelector.getSelectedPriority();

        String error = validateInput(title, categoryName, dueDateStr, selectedPriority);
        if (error != null) {
            showToast(error);
            return false;
        }

        Map<String, String> nameToId = new HashMap<>();
        List<Category> categories = taskViewModel.getCategoryList().getValue();
        if (categories != null) {
            for (Category c : categories) {
                nameToId.put(c.getName(), c.getId());
            }
        }

        task.setName(title);
        task.setDueDate(DateUtil.parseDate(dueDateStr));
        task.setCategoryId(nameToId.get(categoryName));
        task.setPriority(Priority.fromLabel(selectedPriority).name());
        task.setUpdatedAt(new Date());

        if (isEdit) {
            taskViewModel.updateTask(task);
        } else {
            task.setCreatedAt(new Date());
            taskViewModel.addTask(task);
        }

        return true;
    }

    private String validateInput(String title, String categoryName, String dueDateStr, String selectedPriority) {
        if (title.isEmpty()) return "Tiêu đề không được để trống";
        if (categoryName.isEmpty()) return "Vui lòng chọn danh mục";
        if (dueDateStr.isEmpty()) return "Vui lòng chọn ngày đến hạn";
        if (!DateUtil.isValidDate(dueDateStr)) return "Ngày không hợp lệ";
        if (selectedPriority == null) return "Vui lòng chọn mức độ ưu tiên";
        return null;
    }

    private void showToast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdate(Task task) {
        showTaskDialog(task, true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}