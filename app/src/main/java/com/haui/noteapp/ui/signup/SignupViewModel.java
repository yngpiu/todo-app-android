package com.haui.noteapp.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;
import com.haui.noteapp.repository.UserRepository;

public class SignupViewModel extends ViewModel {
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final UserRepository userRepository = new UserRepository();

    public void signUp(String email, String password, User user) {
        userRepository.signUp(email, password, user, new IFirebaseCallbackListener<Void>() {
            @Override
            public void onFirebaseLoadSuccess(Void unused) {
                signUpSuccess.postValue(true);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
