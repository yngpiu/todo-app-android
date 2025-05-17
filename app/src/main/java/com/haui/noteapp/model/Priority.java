package com.haui.noteapp.model;

public enum Priority {
    HIGH("Cao"),
    MEDIUM("Trung bình"),
    LOW("Thấp");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static Priority fromLabel(String label) {
        for (Priority p : values()) {
            if (p.label.equals(label)) return p;
        }
        return LOW;
    }

    public static Priority fromKey(String key) {
        try {
            return Priority.valueOf(key);
        } catch (IllegalArgumentException | NullPointerException e) {
            return LOW;
        }
    }
}
