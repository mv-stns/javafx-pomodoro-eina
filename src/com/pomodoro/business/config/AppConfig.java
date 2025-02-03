package com.pomodoro.business.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javafx.beans.property.SimpleIntegerProperty;

public class AppConfig {
  private static final Properties properties = new Properties();
  public static final boolean DEBUG_MODE;

  // Customizable durations
  public static int FOCUS_DURATION;
  public static int SHORT_BREAK_DURATION;
  public static int LONG_BREAK_DURATION;

  private static final SimpleIntegerProperty focusDurationProperty = new SimpleIntegerProperty();
  private static final SimpleIntegerProperty shortBreakDurationProperty = new SimpleIntegerProperty();
  private static final SimpleIntegerProperty longBreakDurationProperty = new SimpleIntegerProperty();

  static {
    try (FileInputStream input = new FileInputStream("src/resources/application.properties")) {
      properties.load(input);
      DEBUG_MODE = Boolean.parseBoolean(properties.getProperty("DEBUG_MODE", "false"));
      loadDefaultDurations();
    } catch (IOException e) {
      throw new RuntimeException("Error loading application.properties", e);
    }
  }

  public static void loadDefaultDurations() {
    if (DEBUG_MODE) {
      setFocusDuration(Integer.parseInt(properties.getProperty("DEBUG_FOCUS_DURATION", "5")));
      setShortBreakDuration(Integer.parseInt(properties.getProperty("DEBUG_SHORT_BREAK_DURATION", "5")));
      setLongBreakDuration(Integer.parseInt(properties.getProperty("DEBUG_LONG_BREAK_DURATION", "5")));
    } else {
      setFocusDuration(Integer.parseInt(properties.getProperty("FOCUS_DURATION", "25")));
      setShortBreakDuration(Integer.parseInt(properties.getProperty("SHORT_BREAK_DURATION", "5")));
      setLongBreakDuration(Integer.parseInt(properties.getProperty("LONG_BREAK_DURATION", "15")));
    }
  }

  public static void setFocusDuration(int minutes) {
    FOCUS_DURATION = DEBUG_MODE ? minutes : minutes * 60;
    focusDurationProperty.set(FOCUS_DURATION);
  }

  public static void setShortBreakDuration(int minutes) {
    SHORT_BREAK_DURATION = DEBUG_MODE ? minutes : minutes * 60;
    shortBreakDurationProperty.set(SHORT_BREAK_DURATION);
  }

  public static void setLongBreakDuration(int minutes) {
    LONG_BREAK_DURATION = DEBUG_MODE ? minutes : minutes * 60;
    longBreakDurationProperty.set(LONG_BREAK_DURATION);
  }

  // Getter für die Properties
  public static SimpleIntegerProperty focusDurationProperty() {
    return focusDurationProperty;
  }

  public static SimpleIntegerProperty shortBreakDurationProperty() {
    return shortBreakDurationProperty;
  }

  public static SimpleIntegerProperty longBreakDurationProperty() {
    return longBreakDurationProperty;
  }

}
