package com.haui.noteapp.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.model.Category;
import com.haui.noteapp.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private CategoryRepository repository = new CategoryRepository();

    public LiveData<List<Category>> getCategoriesLiveData() {
        return categoriesLiveData;
    }

    public void loadCategories() {
        repository.getCategories(new CategoryRepository.DataCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> data) {
                categoriesLiveData.postValue(data);
            }

            @Override
            public void onError(Exception e) {
                // log hoặc xử lý lỗi
            }
        });
    }
}
