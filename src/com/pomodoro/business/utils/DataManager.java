package com.pomodoro.business.utils;

import com.pomodoro.business.Category;
import com.pomodoro.business.PomoPhase;
import com.pomodoro.business.Session;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
  private static final String BASE_DIR = "tomodoro_data";
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss");

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

  public static void saveSession(Session session) {
    try {
      // Create directory for this session
      String sessionDirName = session.getStartTime().format(TIME_FORMAT);
      Path sessionDir = getSessionDir().resolve(sessionDirName);
      Files.createDirectories(sessionDir);

      // Save metadata
      StringBuilder metadata = new StringBuilder();
      metadata.append("startTime=").append(session.getStartTime()).append("\n");
      metadata.append("endTime=").append(session.getEndTime()).append("\n");
      metadata.append("status=").append(session.getStatus()).append("\n");
      metadata.append("mood=").append(session.getMood()).append("\n");
      metadata.append("phase=").append(session.getPhase()).append("\n");

      // Save categories
      metadata.append("categories=");
      List<Category> categories = session.getCategories();
      for (int i = 0; i < categories.size(); i++) {
        metadata.append(categories.get(i).getName());
        if (i < categories.size() - 1) {
          metadata.append(",");
        }
      }
      metadata.append("\n");

      // Write metadata
      Files.writeString(sessionDir.resolve("session.txt"), metadata.toString());

      // Save notes separately if they exist
      if (session.getNotes() != null && !session.getNotes().isEmpty()) {
        Files.writeString(sessionDir.resolve("notes.txt"), session.getNotes());
      }

    } catch (IOException e) {
      System.err.println("Error saving session: " + e.getMessage());
    }
  }

  public static List<Session> loadTodaysSessions() {
    List<Session> sessions = new ArrayList<>();
    try {
      Path todayDir = getSessionDir();
      if (!Files.exists(todayDir)) {
        return sessions;
      }

      // List all session directories for today
      Files.list(todayDir)
          .filter(path -> Files.isDirectory(path))
          .forEach(
              sessionDir -> {
                Session session = loadSession(sessionDir);
                if (session != null) {
                  sessions.add(session);
                }
              });

    } catch (IOException e) {
      System.err.println("Error loading sessions: " + e.getMessage());
    }
    return sessions;
  }

  private static Session loadSession(Path sessionDir) {
    try {
      // Read metadata
      List<String> lines = Files.readAllLines(sessionDir.resolve("session.txt"));
      Session session = null;

      // Parse metadata
      for (String line : lines) {
        String[] parts = line.split("=", 2);
        if (parts.length != 2) continue;

        String key = parts[0];
        String value = parts[1];

        if (key.equals("phase")) {
          session = new Session(PomoPhase.valueOf(value));
        } else if (session != null) {
          switch (key) {
            case "status":
              // Set status based on value
              break;
            case "mood":
              session.setMood(value);
              break;
            case "categories":
              if (!value.isEmpty()) {
                String[] categoryNames = value.split(",");
                for (String name : categoryNames) {
                  session.addCategory(new Category(name));
                }
              }
              break;
          }
        }
      }

      // Load notes if they exist
      Path notesPath = sessionDir.resolve("notes.txt");
      if (Files.exists(notesPath)) {
        String notes = Files.readString(notesPath);
        session.setNotes(notes);
      }

      return session;

    } catch (IOException e) {
      System.err.println("Error loading session: " + e.getMessage());
      return null;
    }
  }
}
