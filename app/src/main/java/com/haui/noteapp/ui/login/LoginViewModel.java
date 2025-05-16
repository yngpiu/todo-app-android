package com.haui.noteapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.repository.AuthRepository;

public class LoginViewModel extends ViewModel {
    private AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;
    private LiveData<String> errorLiveData;
    private LiveData<Boolean> loadingLiveData;


    public LoginViewModel() {
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
        errorLiveData = authRepository.getErrorLiveData();
        loadingLiveData = authRepository.getLoadingLiveData();
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }
}
