package com.pomodoro.business.audio;

import de.hsrm.mi.eibo.simpleplayer.SimpleAudioPlayer;
import de.hsrm.mi.eibo.simpleplayer.SimpleMinim;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private final Map<String, AudioPlayer> activePlayers;
    private final Object lock = new Object();

    public AudioManager() {
        this.activePlayers = new HashMap<>();
    }

    public void playSound(Sound sound, float volume) {
        synchronized (lock) {
            String soundId = sound.getSoundCategory() + "_" + sound.getSoundname();
            if (!activePlayers.containsKey(soundId)) {
                AudioPlayer player = new AudioPlayer(sound);
                activePlayers.put(soundId, player);

                new Thread(() -> {
                    try {
                        player.setVolume(volume);
                        player.getPlayer().loop();
                    } catch (Exception e) {
                        System.err.println("Error playing sound: " + sound.getSoundname());
                        synchronized (lock) {
                            activePlayers.remove(soundId);
                        }
                    }
                }).start();
            }
        }
    }

    public void stopSound(Sound sound) {
        String soundId = sound.getSoundCategory() + "_" + sound.getSoundname();
        synchronized (lock) {
            AudioPlayer player = activePlayers.get(soundId);
            if (player != null) {
                new Thread(() -> {
                    try {
                        player.getPlayer().pause();
                        player.close();
                        synchronized (lock) {
                            activePlayers.remove(soundId);
                        }
                    } catch (Exception e) {
                        System.err.println("Error stopping sound: " + sound.getSoundname());
                    }
                }).start();
            }
        }
    }

    private static class AudioPlayer {
        private final SimpleMinim minim;
        private final SimpleAudioPlayer player;

        public AudioPlayer(Sound sound) {
            this.minim = new SimpleMinim();
            this.player = minim.loadMP3File(sound.getSoundFilePath());
        }

        public SimpleAudioPlayer getPlayer() {
            return player;
        }

        public void setVolume(float volume) {
            float normalizedVolume = normalizeVolume(volume);
            player.setGain(normalizedVolume);
        }

        public void close() {
            minim.stop();
        }

        private float normalizeVolume(float percentage) {
            percentage = Math.max(0, Math.min(100, percentage));
            float minPercentage = 0.01f;
            float gainDB = 20 * (float) Math.log10(Math.max(percentage, minPercentage) / 100);
            return Math.max(gainDB, -80);
        }
    }

    public void setVolume(Sound sound, float volume) {
        String soundId = sound.getSoundCategory() + "_" + sound.getSoundname();
        synchronized (lock) {
            AudioPlayer player = activePlayers.get(soundId);
            if (player != null) {
                new Thread(() -> {
                    player.setVolume(volume);
                }).start();
            }
        }
    }

    public void stopAll() {
        synchronized (lock) {
            for (AudioPlayer player : activePlayers.values()) {
                new Thread(() -> {
                    player.getPlayer().pause();
                    player.close();
                }).start();
            }
            activePlayers.clear();
        }
    }

    public boolean isPlaying(Sound sound) {
        String soundId = sound.getSoundCategory() + "_" + sound.getSoundname();
        synchronized (lock) {
            AudioPlayer player = activePlayers.get(soundId);
            return player != null && player.getPlayer().isPlaying();
        }
    }
}
