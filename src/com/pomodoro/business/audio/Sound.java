package com.pomodoro.business.audio;

public class Sound {

    private String soundName;
    private String soundFilePath;
    private String soundCategory;

    public Sound(String filePath, String name, String soundCategory) {
        this.soundFilePath = filePath;
        this.soundName = name;
        this.soundCategory = soundCategory;
    }

    public String getSoundFilePath() {
        return soundFilePath;
    }

    public String getSoundname() {
        return soundName;
    }

    public String getSoundCategory() {
        return soundCategory;
    }

}
