package com.pomodoro.business.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataManager {
  private static final String BASE_DIR = "tomodoro_data";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static void ensureDirectoryStructure() {
    try {
      Files.createDirectories(getSessionDir());
    } catch (IOException e) {
      System.err.println("Error creating directory structure: " + e.getMessage());
    }
  }

  public static Path getSessionDir() {
    LocalDate today = LocalDate.now();
    return Paths.get(BASE_DIR, today.format(DATE_FORMAT));
  }

  public static Path getNotesFile() {
    return getSessionDir().resolve("notes.txt");
  }

  public static Path getTasksFile() {
    return getSessionDir().resolve("tasks.txt");
  }

  public static String loadTodaysNotes() {
    try {
      Path notesPath = getNotesFile();
      if (Files.exists(notesPath)) {
        return Files.readString(notesPath);
      }
    } catch (IOException e) {
      System.err.println("Error loading notes: " + e.getMessage());
    }
    return "";
  }

  public static void saveTodaysNotes(String content) {
    try {
      ensureDirectoryStructure();
      Files.writeString(getNotesFile(), content);
    } catch (IOException e) {
      System.err.println("Error saving notes: " + e.getMessage());
    }
  }

  public static void saveTodaysTasks(String content) {
    try {
      ensureDirectoryStructure();
      Files.writeString(getTasksFile(), content);
    } catch (IOException e) {
      System.err.println("Error saving tasks: " + e.getMessage());
    }
  }

  public static String loadTodaysTasks() {
    try {
      Path tasksPath = getTasksFile();
      if (Files.exists(tasksPath)) {
        return Files.readString(tasksPath);
      }
    } catch (IOException e) {
      System.err.println("Error loading tasks: " + e.getMessage());
    }
    return "";
  }
}
