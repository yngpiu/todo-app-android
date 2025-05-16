package com.haui.noteapp.listener;

public interface IFirebaseCallbackListener<T> {
    void onFirebaseLoadSuccess(T items);
    void onFirebaseLoadFailed(String errorMessage);
}
