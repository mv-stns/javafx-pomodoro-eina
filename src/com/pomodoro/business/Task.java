package com.pomodoro.business;

import java.util.Date;

public class Task {
  private Date finishedAt;
  private String taskName;

  public String getTaskName() {
    return taskName;
  }

  private boolean isCompleted = false;

  public Task(String s) {
    taskName = s;
    new Date();
  }

  public void setFinished() {
    if (finishedAt == null) {
      finishedAt = new Date();
      isCompleted = !isCompleted;
    } else {
      finishedAt = null;
      isCompleted = !isCompleted;
    }
  }

  public boolean getCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean b) {
    isCompleted = b;
  }
}
