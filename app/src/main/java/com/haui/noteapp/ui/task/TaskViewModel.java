package com.haui.noteapp.ui.task;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.Category;
import com.haui.noteapp.model.Task;
import com.haui.noteapp.repository.CategoryRepository;
import com.haui.noteapp.repository.TaskRepository;
import com.haui.noteapp.util.Event;

import java.util.List;

public class TaskViewModel extends ViewModel {

    private final MutableLiveData<List<Task>> taskList = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> actionMessage = new MutableLiveData<>();
    private final MediatorLiveData<Pair<List<Task>, List<Category>>> combinedData = new MediatorLiveData<>();
    private final TaskRepository repository = new TaskRepository();
    private final CategoryRepository categoryRepository = new CategoryRepository();

    public TaskViewModel() {
        loadCategories();
        loadTasks();
        combineTaskAndCategory();
    }

    public MediatorLiveData<Pair<List<Task>, List<Category>>> getCombinedData() {
        return combinedData;
    }
    public LiveData<List<Category>> getCategoryList() {
        return categoryList;
    }

    public LiveData<List<Task>> getTaskList() {
        return taskList;
    }

    public LiveData<Event<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Event<String>> getActionMessage() {
        return actionMessage;
    }

    private void combineTaskAndCategory() {
        combinedData.addSource(taskList, tasks -> {
            List<Category> categories = categoryList.getValue();
            if (categories != null) {
                combinedData.setValue(new Pair<>(tasks, categories));
            }
        });

        combinedData.addSource(categoryList, categories -> {
            List<Task> tasks = taskList.getValue();
            if (tasks != null) {
                combinedData.setValue(new Pair<>(tasks, categories));
            }
        });
    }

    public void loadCategories() {
        categoryRepository.loadData(new IFirebaseCallbackListener<>() {

            public void onFirebaseLoadSuccess(List<Category> data) {
                categoryList.setValue(data);
            }
            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }

    public void loadTasks() {
        repository.loadData(new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(List<Task> data) {
                taskList.setValue(data);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }

    public void addTask(Task task) {
        repository.addTask(task, new IFirebaseCallbackListener<>() {
            @Override
            public void onFirebaseLoadSuccess(Void data) {
                actionMessage.postValue(new Event<>("Đã thêm nhiệm vụ"));
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.postValue(new Event<>(message));
            }
        });
    }

    public void deleteTask(String taskId) {
        Log.d("task", taskId);
    }


}