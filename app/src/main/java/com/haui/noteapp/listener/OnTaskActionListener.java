package com.haui.noteapp.listener;

import com.haui.noteapp.model.Task;

public interface OnTaskActionListener {
    void onUpdate(Task task);

    void onDelete(Task task);

    void onCompleteTask(Task task);
}