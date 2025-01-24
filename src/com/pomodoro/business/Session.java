package com.pomodoro.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private SessionStatus status;
  private String notes;
  private List<Category> categories;
  private String mood;
  private PomoPhase phase;

  public enum SessionStatus {
    COMPLETED, // Session ran full duration
    INTERRUPTED, // Session was manually stopped
    IN_PROGRESS // Session is currently running
  }

  public Session(PomoPhase phase) {
    this.phase = phase;
    this.startTime = LocalDateTime.now();
    this.status = SessionStatus.IN_PROGRESS;
    this.categories = new ArrayList<>();
  }

  public void complete() {
    this.endTime = LocalDateTime.now();
    this.status = SessionStatus.COMPLETED;
  }

  public void interrupt() {
    this.endTime = LocalDateTime.now();
    this.status = SessionStatus.INTERRUPTED;
  }

  public void addCategory(Category category) {
    if (!categories.contains(category)) {
      categories.add(category);
    }
  }

  public void removeCategory(Category category) {
    categories.remove(category);
  }

  public void setMood(String mood) {
    this.mood = mood;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  // Getters
  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public SessionStatus getStatus() {
    return status;
  }

  public String getNotes() {
    return notes;
  }

  public List<Category> getCategories() {
    return new ArrayList<>(categories);
  }

  public String getMood() {
    return mood;
  }

  public PomoPhase getPhase() {
    return phase;
  }

  // Duration calculation
  public long getDurationInSeconds() {
    if (endTime == null) return 0;
    return java.time.Duration.between(startTime, endTime).getSeconds();
  }
}
