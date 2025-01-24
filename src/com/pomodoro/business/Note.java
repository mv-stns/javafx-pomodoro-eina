package com.pomodoro.business;

import java.util.Date;

public class Note {
    private Date createdAt, finishedAt;
    private String taskName;

    public Note(String s) {
        taskName = s;
        createdAt = new Date();
    }

    public void setFinished() {
        if (finishedAt == null) {
            finishedAt = new Date();
        } else {
            finishedAt = null;
        }
    }
}
