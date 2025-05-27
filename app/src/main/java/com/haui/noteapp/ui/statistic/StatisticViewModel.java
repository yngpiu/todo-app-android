package com.haui.noteapp.ui.statistic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.repository.StatisticRepository;
import com.haui.noteapp.util.Event;

import java.util.Map;

public class StatisticViewModel extends ViewModel {

    private final MutableLiveData<StatisticData> statisticData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> priorityStats = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Map<String, Integer>>> categoryStats = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final StatisticRepository repository = new StatisticRepository();

    public StatisticViewModel() {
        loadStatistics();
        loadPriorityStats();
        loadCategoryStats();
    }

    public LiveData<StatisticData> getStatisticData() {
        return statisticData;
    }

    public LiveData<Map<String, Integer>> getPriorityStats() {
        return priorityStats;
    }

    public LiveData<Map<String, Map<String, Integer>>> getCategoryStats() {
        return categoryStats;
    }

    public LiveData<Event<String>> getErrorMessage() {
        return errorMessage;
    }

    private void loadStatistics() {
        repository.getTaskStats(new IFirebaseCallbackListener<StatisticData>() {
            @Override
            public void onFirebaseLoadSuccess(StatisticData data) {
                statisticData.setValue(data);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }

    private void loadPriorityStats() {
        repository.getTaskPriorityStats(new IFirebaseCallbackListener<Map<String, Integer>>() {
            @Override
            public void onFirebaseLoadSuccess(Map<String, Integer> data) {
                priorityStats.setValue(data);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }

    private void loadCategoryStats() {
        repository.getTaskCategoryStats(new IFirebaseCallbackListener<Map<String, Map<String, Integer>>>() {
            @Override
            public void onFirebaseLoadSuccess(Map<String, Map<String, Integer>> data) {
                categoryStats.setValue(data);
            }

            @Override
            public void onFirebaseLoadFailed(String message) {
                errorMessage.setValue(new Event<>(message));
            }
        });
    }
}