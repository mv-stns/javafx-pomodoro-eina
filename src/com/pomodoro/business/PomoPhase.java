package com.pomodoro.business;

public enum PomoPhase {
    FOCUS(25 * 60, "Fokus"),          // 25 Minuten
    SHORT_BREAK(25, "Kurze Pause"), // 5 Minuten
    LONG_BREAK(15 * 60, "Lange Pause"); // 15 Minuten

    private final int durationInSeconds;
    private final String displayName;

    PomoPhase(int durationInSeconds, String displayName) {
        this.durationInSeconds = durationInSeconds;
        this.displayName = displayName;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public String getDisplayName() {
        return displayName;
    }
}
