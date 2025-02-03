package com.pomodoro.business.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SoundManager {
    private static final String SONG_DIRECTORY = "src/resources/audio";
    private static final String PAUSE_SOUND = "Pause.mp3";
    private List<Sound> sounds;
    private boolean initialized = false;

    public SoundManager() {
        sounds = new ArrayList<>();
    }

    public void initializeAsync(Runnable onComplete) {
        new Thread(() -> {
            initSongFiles();
            initialized = true;
            if (onComplete != null) {
                onComplete.run();
            }
        }).start();
    }

    private void initSongFiles() {
        File rootFolder = new File(SONG_DIRECTORY);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            throw new RuntimeException("Audio directory not found: " + SONG_DIRECTORY);
        }

        List<Sound> tempSounds = new ArrayList<>();

        // Durchlaufe alle Kategorie-Ordner
        File[] categoryFolders = rootFolder.listFiles(folder -> folder.isDirectory());
        if (categoryFolders != null) {
            for (File categoryFolder : categoryFolders) {
                String category = categoryFolder.getName();

                // Durchlaufe alle Sound-Dateien in der Kategorie
                File[] soundFiles = categoryFolder.listFiles(
                        (dir, name) -> name.toLowerCase().endsWith(".mp3") ||
                                name.toLowerCase().endsWith(".wav"));

                if (soundFiles != null) {
                    for (File soundFile : soundFiles) {
                        String soundName = soundFile.getName();
                        soundName = soundName.substring(0, soundName.lastIndexOf('.'));

                        Sound sound = new Sound(
                                soundFile.getAbsolutePath(),
                                soundName,
                                category);
                        tempSounds.add(sound);
                    }
                }
            }
        }

        if (tempSounds.isEmpty()) {
            throw new RuntimeException("No audio files found in " + SONG_DIRECTORY);
        }

        sounds = tempSounds;
    }

    public List<Sound> getSoundsByCategory(String category) {
        if (!initialized)
            return new ArrayList<>();
        List<Sound> categorySounds = new ArrayList<>();
        for (Sound sound : sounds) {
            if (sound.getSoundCategory().equals(category)) {
                categorySounds.add(sound);
            }
        }
        return categorySounds;
    }

    public List<String> getCategories() {
        if (!initialized)
            return new ArrayList<>();
        List<String> categories = new ArrayList<>();
        for (Sound sound : sounds) {
            String category = sound.getSoundCategory();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        return categories;
    }

    public Sound getSound(String name, String category) {
        if (!initialized)
            return null;
        for (Sound sound : sounds) {
            if (sound.getSoundname().equals(name) &&
                    sound.getSoundCategory().equals(category)) {
                return sound;
            }
        }
        return null;
    }

    public List<Sound> getAllSounds() {
        if (!initialized)
            return new ArrayList<>();
        return new ArrayList<>(sounds);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
