package com.haui.noteapp.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.repository.AuthRepository;

public class SignupViewModel extends ViewModel {
    private AuthRepository authRepository;
    private LiveData<FirebaseUser> userLiveData;
    private LiveData<String> errorLiveData;
    private LiveData<Boolean> loadingLiveData;

    public SignupViewModel() {
        authRepository = new AuthRepository();
        userLiveData = authRepository.getUserLiveData();
        errorLiveData = authRepository.getErrorLiveData();
        loadingLiveData = authRepository.getLoadingLiveData();
    }

    public void signup(String email, String password, String displayName) {
        authRepository.signup(email, password, displayName);
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
