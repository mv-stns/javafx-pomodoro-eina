package com.pomodoro.business;

import com.pomodoro.business.config.AppConfig;

public enum PomoPhase {
  FOCUS(AppConfig.FOCUS_DURATION, "Fokus"),
  SHORT_BREAK(AppConfig.SHORT_BREAK_DURATION, "Kurze Pause"),
  LONG_BREAK(AppConfig.LONG_BREAK_DURATION, "Lange Pause");

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
