package com.haui.noteapp.ui.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.haui.noteapp.repository.UserRepository;

public class SettingViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

    public SettingViewModel() {
        userRepository = new UserRepository();
        userLiveData.setValue(FirebaseAuth.getInstance().getCurrentUser());
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public void signOut() {
        userRepository.signOut();
        userLiveData.setValue(null);
    }

}
