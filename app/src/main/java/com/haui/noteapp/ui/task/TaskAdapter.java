package com.haui.noteapp.ui.task;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
        holder.binding.tvTaskDate.setText(sdf.format(task.getDouDate()));
        String categoryName = categoryMap.containsKey(task.getCategoryId())
                ? categoryMap.get(task.getCategoryId()).getName()
                : "Không rõ";

        String priorityKey = task.getPriority();
        String priorityLabel = Priority.fromKey(priorityKey).getLabel();
        holder.binding.tvTaskPriority.setText(priorityLabel);

        // Apply strikethrough to title if task is completed
        if (task.isCompleted()) {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.binding.tvTaskTitle.setPaintFlags(holder.binding.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Set checkbox state
        holder.binding.cbComplete.setChecked(task.isCompleted());

        // Add checkbox change listener
        holder.binding.cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked); // Update the task's isCompleted field
            if (listener != null) {
                listener.onUpdate(task); // Trigger update via listener
            }
            // Update strikethrough based on new isCompleted state
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
                    listener.onDelete(task); // Trigger delete via listener
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

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        ItemTaskBinding binding;

        public TaskViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}