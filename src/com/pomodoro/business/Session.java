package com.pomodoro.business;

import java.time.Duration;
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
  private int targetDurationSeconds;
  private int actualDurationSeconds;
  private boolean completed;
  private String interruptionReason;

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
    this.targetDurationSeconds = phase.getDurationInSeconds();
  }

  public void complete() {
    this.endTime = LocalDateTime.now();
    this.status = SessionStatus.COMPLETED;
    this.completed = true;
    this.actualDurationSeconds = (int) getDurationInSeconds();
  }

  public void interrupt(String reason) {
    this.endTime = LocalDateTime.now();
    this.status = SessionStatus.INTERRUPTED;
    this.completed = false;
    this.interruptionReason = reason;
    this.actualDurationSeconds = (int) getDurationInSeconds();
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

  public long getDurationInSeconds() {
    if (endTime == null)
      return 0;
    return Duration.between(startTime, endTime).getSeconds();
  }

  public int getTargetDurationSeconds() {
    return targetDurationSeconds;
  }

  public int getActualDurationSeconds() {
    return actualDurationSeconds;
  }

  public boolean isCompleted() {
    return completed;
  }

  public String getInterruptionReason() {
    return interruptionReason;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public void setStatus(SessionStatus status) {
    this.status = status;
  }

  public void setPhase(PomoPhase phase) {
    this.phase = phase;
  }

  public void setTargetDurationSeconds(int targetDurationSeconds) {
    this.targetDurationSeconds = targetDurationSeconds;
  }

  public void setActualDurationSeconds(int actualDurationSeconds) {
    this.actualDurationSeconds = actualDurationSeconds;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public void setInterruptionReason(String interruptionReason) {
    this.interruptionReason = interruptionReason;
  }
}
