package com.pomodoro.business;

import com.pomodoro.business.utils.DataManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SessionManager {
  private static SessionManager instance;
  private final ObjectProperty<Session> currentSession = new SimpleObjectProperty<>();
  private final ObjectProperty<Note> currentNote = new SimpleObjectProperty<>();

  private SessionManager() {

    String savedNotes = DataManager.loadTodaysNotes();
    if (!savedNotes.isEmpty()) {
      setCurrentNote(new Note(savedNotes));
    }
  }

  public static SessionManager getInstance() {
    if (instance == null) {
      instance = new SessionManager();
    }
    return instance;
  }

  public void setCurrentSession(Session session) {
    currentSession.set(session);
  }

  public Session getCurrentSession() {
    return currentSession.get();
  }

  public ObjectProperty<Session> currentSessionProperty() {
    return currentSession;
  }

  public void setCurrentNote(Note note) {
    currentNote.set(note);
    if (note != null) {
      DataManager.saveTodaysNotes(note.getNoteData());
    }
  }

  public Note getCurrentNote() {
    return currentNote.get();
  }

  public ObjectProperty<Note> currentNoteProperty() {
    return currentNote;
  }
}
