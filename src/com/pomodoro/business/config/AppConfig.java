package com.pomodoro.business.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
  private static final Properties properties = new Properties();
  public static final boolean DEBUG_MODE;

  // Customizable durations
  public static int FOCUS_DURATION;
  public static int SHORT_BREAK_DURATION;
  public static int LONG_BREAK_DURATION;

  static {
    try (FileInputStream input = new FileInputStream("src/resources/application.properties")) {
      properties.load(input);
      DEBUG_MODE = Boolean.parseBoolean(properties.getProperty("DEBUG_MODE", "false"));
      loadDefaultDurations();
    } catch (IOException e) {
      throw new RuntimeException("Error loading application.properties", e);
    }
  }

  private static void loadDefaultDurations() {
    if (DEBUG_MODE) {
      FOCUS_DURATION = Integer.parseInt(properties.getProperty("DEBUG_FOCUS_DURATION", "5"));
      SHORT_BREAK_DURATION =
          Integer.parseInt(properties.getProperty("DEBUG_SHORT_BREAK_DURATION", "5"));
      LONG_BREAK_DURATION =
          Integer.parseInt(properties.getProperty("DEBUG_LONG_BREAK_DURATION", "5"));
    } else {
      FOCUS_DURATION = Integer.parseInt(properties.getProperty("FOCUS_DURATION", "25")) * 60;
      SHORT_BREAK_DURATION =
          Integer.parseInt(properties.getProperty("SHORT_BREAK_DURATION", "5")) * 60;
      LONG_BREAK_DURATION =
          Integer.parseInt(properties.getProperty("LONG_BREAK_DURATION", "15")) * 60;
    }
  }

  // Setter methods for customizing durations
  public static void setFocusDuration(int minutes) {
    FOCUS_DURATION = DEBUG_MODE ? minutes : minutes * 60;
  }

  public static void setShortBreakDuration(int minutes) {
    SHORT_BREAK_DURATION = DEBUG_MODE ? minutes : minutes * 60;
  }

  public static void setLongBreakDuration(int minutes) {
    LONG_BREAK_DURATION = DEBUG_MODE ? minutes : minutes * 60;
  }

  public static void resetToDefaults() {
    loadDefaultDurations();
  }
}
