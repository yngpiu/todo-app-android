package com.haui.noteapp.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.repository.CategoryRepository;
import com.haui.noteapp.util.Event;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> actionMessage = new MutableLiveData<>();

    private final CategoryRepository repository = new CategoryRepository();

    public CategoryViewModel() {
        loadCategories();
    }

    public void loadCategories() {
        repository.loadData(new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(List<Category> data) {
                categoryList.setValue(data);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }

    public void addCategory(Category category) {
        repository.addCategory(category, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(Void data) {
                actionMessage.postValue(new Event<>("Đã thêm danh mục"));
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(new Event<>(message));
            }
        });
    }

    public void updateCategory(Category category) {
        repository.updateCategory(category, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(Void data) {
                actionMessage.postValue(new Event<>("Đã sửa danh mục"));
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(new Event<>(message));
            }
        });
    }

    public void deleteCategory(String categoryId) {
        repository.deleteCategory(categoryId, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(Void data) {
                actionMessage.postValue(new Event<>("Đã xoá danh mục"));
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(new Event<>(message));
            }
        });
    }

    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public LiveData<Event<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Event<String>> getActionMessage() {
        return actionMessage;
    }
}
