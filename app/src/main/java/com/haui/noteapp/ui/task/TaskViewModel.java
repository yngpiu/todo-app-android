package com.haui.noteapp.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is task fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}