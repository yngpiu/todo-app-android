package com.haui.noteapp.ui.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.User;
import com.haui.noteapp.repository.UserRepository;
import com.haui.noteapp.util.Event;

public class SignupViewModel extends ViewModel {

    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> actionMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();

    private final UserRepository userRepository = new UserRepository();

    public void signUp(String email, String password, User user) {
        userRepository.signUp(email, password, user, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(Void unused) {
                actionMessage.postValue(new Event<>("Đăng ký thành công"));
                signUpSuccess.postValue(true);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(new Event<>(message));
            }
        });
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<Event<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Event<String>> getActionMessage() {
        return actionMessage;
    }
}
