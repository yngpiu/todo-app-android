package com.haui.noteapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.repository.UserRepository;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final UserRepository userRepository = new UserRepository();

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String email, String password) {
        userRepository.login(email, password, new IFirebaseCallbackListener<FirebaseUser>() {
            @Override
            public void onFirebaseLoadSuccess(FirebaseUser data) {
                loginSuccess.setValue(true);
            }

            @Override
            public void onFirebaseLoadFailed(String error) {
                errorMessage.setValue(error);
            }
        });
    }
}
