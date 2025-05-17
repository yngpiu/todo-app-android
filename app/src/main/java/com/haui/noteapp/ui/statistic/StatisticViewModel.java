package com.haui.noteapp.ui.statistic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.haui.noteapp.listener.IFirebaseCallbackListener;
import com.haui.noteapp.model.StatisticData;
import com.haui.noteapp.repository.StatisticRepository;
import com.haui.noteapp.util.Event;

public class StatisticViewModel extends ViewModel {

    private final MutableLiveData<StatisticData> statisticData = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessage = new MutableLiveData<>();
    private final StatisticRepository repository = new StatisticRepository();

    public StatisticViewModel() {
        loadStatistics();
    }

    public LiveData<StatisticData> getStatisticData() {
        return statisticData;
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
}