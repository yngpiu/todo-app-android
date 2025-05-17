package com.haui.noteapp.model;

public class StatisticData {
    private final int total;
    private final int completed;
    private final int pending;
    private final int completionRate;

    public StatisticData(int total, int completed, int pending, int completionRate) {
        this.total = total;
        this.completed = completed;
        this.pending = pending;
        this.completionRate = completionRate;
    }

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public int getPending() {
        return pending;
    }

    public int getCompletionRate() {
        return completionRate;
    }
}
