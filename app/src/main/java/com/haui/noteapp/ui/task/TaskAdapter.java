package com.haui.noteapp.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haui.noteapp.R;
import com.haui.noteapp.databinding.ItemTaskBinding;
import com.haui.noteapp.listener.OnTaskActionListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Task> tasks;
    //    private final OnTaskActionListener listener;
    private Map<String, Category> categoryMap;

    public TaskAdapter(List<Task> tasks, Map<String, Category> categoryMap) {
        this.tasks = tasks;
        this.categoryMap = categoryMap;
//        this.listener = listener;
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
        holder.binding.tvTaskCategory.setText(categoryName);
        holder.binding.tvTaskPriority.setText(task.getPriority());


        holder.binding.ivMore.setOnClickListener(v -> {
            android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    // listener.onEditClicked(task);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    // listener.onDeleteClicked(task);
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
