package com.haui.noteapp.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.model.User;

public class AuthRepository {
    private final FirebaseAuth mAuth;
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> errorLiveData;
    private final MutableLiveData<Boolean> loadingLiveData;


    public AuthRepository() {
        mAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        userLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
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

    public void login(String email, String password) {
        loadingLiveData.postValue(true);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                userLiveData.postValue(mAuth.getCurrentUser());
                loadingLiveData.postValue(false);
            } else {
                errorLiveData.postValue(task.getException().getMessage());
                loadingLiveData.postValue(false);
            }
        });

    }

    public Boolean isLogged() {
        return mAuth.getCurrentUser() != null;
    }

    public void signup(String email, String password, String displayName) {
        loadingLiveData.postValue(true);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User user = new User(firebaseUser.getUid(), email, displayName);
                    userRepository.createUser(user);
                    userLiveData.postValue(firebaseUser);
                }
                loadingLiveData.postValue(false);
            } else {
                errorLiveData.postValue(task.getException().getMessage());
                loadingLiveData.postValue(false);
            }
        });
    }
}
