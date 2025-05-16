package com.haui.noteapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.repository.UserRepository;
import com.haui.noteapp.util.Event;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> actionMessage = new MutableLiveData<>();

    private final UserRepository userRepository = new UserRepository();

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Event<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Event<String>> getActionMessage() {
        return actionMessage;
    }

    public void login(String email, String password) {
        userRepository.login(email, password, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(FirebaseUser data) {
                actionMessage.postValue(new Event<>("Đăng nhập thành công"));
                loginSuccess.postValue(true);
            }

            @Override
            public void onFirebaseLoadFailed(String error) {
                errorMessage.postValue(new Event<>(error));
            }
        });
    }
}
