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
    private final MutableLiveData<Boolean> addSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();

    public LiveData<Boolean> getAddSuccess() {
        return addSuccess;
    }

    public LiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    private CategoryRepository categoryRepository;

    public CategoryViewModel() {
        categoryRepository = new CategoryRepository(this);
    }

    public void loadData() {
        categoryRepository.loadData();
    }

    public void addCategory(Category category) {
        categoryRepository.addCategory(category, new IFirebaseCallbackListener<Void>() {
            @Override
            public void onFirebaseLoadSuccess(Void items) {
                addSuccess.postValue(true);
                loadData();
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(message);
            }
        });
    }


    public void deleteCategory(String categoryId) {
        categoryRepository.deleteCategory(categoryId, new IFirebaseCallbackListener<Void>() {
            @Override
            public void onFirebaseLoadSuccess(Void unused) {
                deleteSuccess.postValue(true);
                loadData();
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(message);
            }
        });
    }

    public void updateCategory(Category category) {
        categoryRepository.updateCategory(category, new IFirebaseCallbackListener<Void>() {
            @Override
            public void onFirebaseLoadSuccess(Void items) {
                loadData();
                updateSuccess.postValue(true);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(message);
            }
        });
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
