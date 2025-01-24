package com.pomodoro.business;

import java.util.Date;

public class Note {
  private Date createdAt;
  private String noteData;

  public Note(String noteText) {
    this.noteData = noteText;
    this.createdAt = new Date();
  }

  public String getNoteData() {
    return noteData;
  }

  public void setNoteData(String noteData) {
    this.noteData = noteData;
  }

  public Date getCreatedAt() {
    return createdAt;
  }
}
