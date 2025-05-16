package com.haui.noteapp.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.repository.AuthRepository;

public class SettingViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;


    public SettingViewModel() {
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public void signOut() {
        authRepository.signOut();
    }
}
