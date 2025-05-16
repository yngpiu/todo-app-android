package com.haui.noteapp.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.MainActivity;
import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel implements IFirebaseCallbackListener<List<Category>> {
    private final MutableLiveData<List<Category>> mutableCategoryList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private CategoryRepository categoryRepository;

    public CategoryViewModel() {
        categoryRepository = new CategoryRepository(this);
    }

    public void loadData() {
        categoryRepository.loadData();
    }

    public LiveData<List<Category>> getCategoryList() {
        return mutableCategoryList;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void onFirebaseLoadSuccess(List<Category> categories) {
        mutableCategoryList.setValue(categories);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        errorMessage.setValue(message);
    }
}
