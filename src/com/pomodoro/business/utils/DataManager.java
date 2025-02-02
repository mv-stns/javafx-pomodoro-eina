package com.pomodoro.business.utils;

import com.pomodoro.business.Category;
import com.pomodoro.business.PomoPhase;
import com.pomodoro.business.Session;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      String sessionDirName = session.getStartTime().format(TIME_FORMAT);
      Path sessionDir = getSessionDir().resolve(sessionDirName);
      Files.createDirectories(sessionDir);

      StringBuilder metadata = new StringBuilder();
      metadata.append("startTime=").append(session.getStartTime()).append("\n");
      metadata.append("endTime=").append(session.getEndTime()).append("\n");
      metadata.append("status=").append(session.getStatus()).append("\n");
      metadata.append("phase=").append(session.getPhase()).append("\n");
      metadata.append("targetDuration=").append(session.getTargetDurationSeconds()).append("\n");
      metadata.append("actualDuration=").append(session.getActualDurationSeconds()).append("\n");
      metadata.append("completed=").append(session.isCompleted()).append("\n");

      if (session.getInterruptionReason() != null) {
        metadata.append("interruptionReason=").append(session.getInterruptionReason()).append("\n");
      }

      if (session.getMood() != null) {
        metadata.append("mood=").append(session.getMood()).append("\n");
      }

      // Kategorien speichern
      metadata.append("categories=");
      List<Category> categories = session.getCategories();
      if (!categories.isEmpty()) {
        metadata.append(String.join(",",
            categories.stream()
                .map(Category::getName)
                .toList()));
      }
      metadata.append("\n");

      Files.writeString(sessionDir.resolve("session.txt"), metadata.toString());

      // Notizen speichern
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
      Path sessionFile = sessionDir.resolve("session.txt");
      if (!Files.exists(sessionFile)) {
        return null;
      }

      List<String> lines = Files.readAllLines(sessionFile);
      Map<String, String> metadata = new HashMap<>();

      // Erst alle Metadaten sammeln
      for (String line : lines) {
        String[] parts = line.split("=", 2);
        if (parts.length == 2) {
          String key = parts[0].trim();
          String value = parts[1].trim();
          if (!value.equals("null") && !value.isEmpty()) {
            metadata.put(key, value);
          }
        }
      }

      // Prüfe ob notwendige Daten vorhanden sind
      if (!metadata.containsKey("phase")) {
        return null;
      }

      try {
        PomoPhase phase = PomoPhase.valueOf(metadata.get("phase"));
        Session session = new Session(phase);

        // Setze die Metadaten, nur wenn sie gültig sind
        if (metadata.containsKey("startTime")) {
          try {
            session.setStartTime(LocalDateTime.parse(metadata.get("startTime")));
          } catch (DateTimeParseException e) {
            System.err.println("Error parsing startTime: " + e.getMessage());
          }
        }

        if (metadata.containsKey("endTime")) {
          try {
            session.setEndTime(LocalDateTime.parse(metadata.get("endTime")));
          } catch (DateTimeParseException e) {
            System.err.println("Error parsing endTime: " + e.getMessage());
          }
        }

        if (metadata.containsKey("status")) {
          try {
            session.setStatus(Session.SessionStatus.valueOf(metadata.get("status")));
          } catch (IllegalArgumentException e) {
            System.err.println("Error parsing status: " + e.getMessage());
          }
        }

        if (metadata.containsKey("mood")) {
          session.setMood(metadata.get("mood"));
        }

        if (metadata.containsKey("targetDuration")) {
          try {
            session.setTargetDurationSeconds(Integer.parseInt(metadata.get("targetDuration")));
          } catch (NumberFormatException e) {
            System.err.println("Error parsing targetDuration: " + e.getMessage());
          }
        }

        if (metadata.containsKey("actualDuration")) {
          try {
            session.setActualDurationSeconds(Integer.parseInt(metadata.get("actualDuration")));
          } catch (NumberFormatException e) {
            System.err.println("Error parsing actualDuration: " + e.getMessage());
          }
        }

        if (metadata.containsKey("completed")) {
          session.setCompleted(Boolean.parseBoolean(metadata.get("completed")));
        }

        if (metadata.containsKey("interruptionReason")) {
          session.setInterruptionReason(metadata.get("interruptionReason"));
        }

        if (metadata.containsKey("categories")) {
          String[] categoryNames = metadata.get("categories").split(",");
          for (String name : categoryNames) {
            if (!name.isEmpty()) {
              session.addCategory(new Category(name));
            }
          }
        }

        // Lade Notizen, falls vorhanden
        Path notesPath = sessionDir.resolve("notes.txt");
        if (Files.exists(notesPath)) {
          String notes = Files.readString(notesPath);
          if (notes != null && !notes.isEmpty()) {
            session.setNotes(notes);
          }
        }

        return session;

      } catch (Exception e) {
        System.err.println("Error creating session from metadata: " + e.getMessage());
        return null;
      }

    } catch (IOException e) {
      System.err.println("Error loading session from " + sessionDir + ": " + e.getMessage());
      return null;
    }
  }

  public static List<Session> loadSessionsForDate(LocalDate date) {
    List<Session> sessions = new ArrayList<>();
    try {
      Path dateDir = Paths.get(BASE_DIR, date.format(DATE_FORMAT));
      if (!Files.exists(dateDir)) {
        return sessions;
      }

      // Durchsuche alle Unterverzeichnisse des Datums
      Files.list(dateDir)
          .filter(path -> Files.isDirectory(path))
          .forEach(sessionDir -> {
            Session session = loadSession(sessionDir);
            if (session != null) {
              sessions.add(session);
            }
          });

    } catch (IOException e) {
      System.err.println("Error loading sessions for date " + date + ": " + e.getMessage());
    }
    return sessions;
  }

  public static List<Session> loadAllSessions() {
    List<Session> allSessions = new ArrayList<>();
    try {
      Path baseDir = Paths.get(BASE_DIR);
      if (!Files.exists(baseDir)) {
        return allSessions;
      }

      // Durchsuche alle Datumsverzeichnisse
      Files.list(baseDir)
          .filter(path -> Files.isDirectory(path))
          .forEach(dateDir -> {
            try {
              // Durchsuche alle Session-Verzeichnisse des Datums
              Files.list(dateDir)
                  .filter(path -> Files.isDirectory(path))
                  .forEach(sessionDir -> {
                    Session session = loadSession(sessionDir);
                    if (session != null) {
                      allSessions.add(session);
                    }
                  });
            } catch (IOException e) {
              System.err.println("Error loading sessions from directory " + dateDir + ": " + e.getMessage());
            }
          });

    } catch (IOException e) {
      System.err.println("Error loading all sessions: " + e.getMessage());
    }
    return allSessions;
  }

  public static List<Session> loadSessionsForLastDays(int days) {
    List<Session> sessions = new ArrayList<>();
    LocalDate today = LocalDate.now();

    for (int i = 0; i < days; i++) {
      LocalDate date = today.minusDays(i);
      sessions.addAll(loadSessionsForDate(date));
    }

    return sessions;
  }

}
