package com.haui.noteapp.listener;

import com.haui.noteapp.model.Task;

public interface OnTaskActionListener {
    void onDelete(Task task);
    void onUpdate(Task task);
}