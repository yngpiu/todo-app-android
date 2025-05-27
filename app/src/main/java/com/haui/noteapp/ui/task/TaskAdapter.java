package com.haui.noteapp.ui.task;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.noteapp.R;
import com.haui.noteapp.databinding.ItemTaskBinding;
import com.haui.noteapp.listener.OnTaskActionListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Priority;
import com.haui.noteapp.model.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Task> tasks;
    private final OnTaskActionListener listener;
    private final Map<String, Category> categoryMap;

    public TaskAdapter(List<Task> tasks, Map<String, Category> categoryMap, OnTaskActionListener listener) {
        this.tasks = tasks;
        this.categoryMap = categoryMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.binding.tvTaskTitle.setText(task.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.binding.tvTaskDate.setText(sdf.format(task.getDueDate()));
        String categoryName = categoryMap.containsKey(task.getCategoryId())
                ? categoryMap.get(task.getCategoryId()).getName()
                : "Không rõ";
        String priorityKey = task.getPriority();
        String priorityLabel = Priority.fromKey(priorityKey).getLabel();
        holder.binding.tvTaskPriority.setText(priorityLabel);
        holder.binding.tvTaskCategory.setText(categoryName);

        // Set category color circle
        if (categoryMap.containsKey(task.getCategoryId())) {
            String colorHex = categoryMap.get(task.getCategoryId()).getColorHex();
            if (colorHex != null && !colorHex.isEmpty()) {
                holder.binding.ivCategoryColor.setColorFilter(android.graphics.Color.parseColor(colorHex));
            } else {
                holder.binding.ivCategoryColor.setColorFilter(android.graphics.Color.GRAY); // Màu mặc định nếu không có
            }
        } else {
            holder.binding.ivCategoryColor.setColorFilter(android.graphics.Color.GRAY); // Màu mặc định nếu không tìm thấy danh mục
        }

        int priorityColor = getPriorityColor(priorityKey, holder.itemView.getContext());
        holder.binding.tvTaskPriority.setTextColor(priorityColor);
        if (task.isCompleted()) {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set checkbox state
        holder.binding.cbComplete.setChecked(task.isCompleted());

        // Add checkbox change listener
        holder.binding.cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            if (listener != null) {
                listener.onCompleteTask(task);
            }
            if (isChecked) {
                holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        holder.binding.ivMore.setOnClickListener(v -> {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onUpdate(task);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDelete(task);
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private int getPriorityColor(String priorityKey, android.content.Context context) {
        switch (priorityKey != null ? priorityKey : "") {
            case "HIGH":
                return ContextCompat.getColor(context, R.color.high_priority);
            case "MEDIUM":
                return ContextCompat.getColor(context, R.color.medium_priority);
            case "LOW":
                return ContextCompat.getColor(context, R.color.low_priority);
            default:
                return ContextCompat.getColor(context, R.color.high_priority); // Mặc định dùng màu high nếu không xác định
        }
    }
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}