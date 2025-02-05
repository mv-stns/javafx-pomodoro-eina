package com.pomodoro.presentation.views.settings.ambientSettings;

import com.pomodoro.business.audio.AudioManager;
import com.pomodoro.business.audio.Sound;
import com.pomodoro.business.utils.FontLoader;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class AmbientSettingsViewController {

    @FXML
    private Button riverButton, wavesButton, campfireButton, windButton, 
                   howlingWindButton, treeWindButton, lightRainButton, 
                   heavyRainButton, thunderButton, rainOnWindowButton, 
                   rainOnUmbrellaButton, rainOnTentButton;

    private Map<Button, Sound> buttonSoundMap;
    private Map<Button, Boolean> soundStates;
    private AudioManager audioManager;
    private static final float DEFAULT_VOLUME = 50.0f;

    @FXML
    private void initialize() {
        audioManager = new AudioManager();
        buttonSoundMap = new HashMap<>();
        soundStates = new HashMap<>();

        initializeSoundMap();
        setupButtons();
    }

    private void initializeSoundMap() {

        buttonSoundMap.put(riverButton, new Sound("src/resources/audio/Natur/Fluss.mp3", "Fluss", "Natur"));
        buttonSoundMap.put(wavesButton, new Sound("src/resources/audio/Natur/Wellen.mp3", "Wellen", "Natur"));
        buttonSoundMap.put(campfireButton, new Sound("src/resources/audio/Natur/Lagerfeuer.mp3", "Lagerfeuer", "Natur"));
        buttonSoundMap.put(windButton, new Sound("src/resources/audio/Natur/Wind.mp3", "Wind", "Natur"));
        buttonSoundMap.put(howlingWindButton, new Sound("src/resources/audio/Natur/heulender Wind.mp3", "Heulender Wind", "Natur"));
        buttonSoundMap.put(treeWindButton, new Sound("src/resources/audio/Natur/Wind in Bäumen.mp3", "Wind in Bäumen", "Natur"));


        buttonSoundMap.put(lightRainButton, new Sound("src/resources/audio/Regen/Leichter Regen.mp3", "Leichter Regen", "Regen"));
        buttonSoundMap.put(heavyRainButton, new Sound("src/resources/audio/Regen/Starker Regen.mp3", "Starker Regen", "Regen"));
        buttonSoundMap.put(thunderButton, new Sound("src/resources/audio/Regen/Donner.mp3", "Donner", "Regen"));
        buttonSoundMap.put(rainOnWindowButton, new Sound("src/resources/audio/Regen/Regen Auf Fenster.mp3", "Regen auf Fenster", "Regen"));
        buttonSoundMap.put(rainOnUmbrellaButton, new Sound("src/resources/audio/Regen/Regen Auf Schirm.mp3", "Regen auf Schirm", "Regen"));
        buttonSoundMap.put(rainOnTentButton, new Sound("src/resources/audio/Regen/Regen Auf Zelt.mp3", "Regen auf Zelt", "Regen"));
    }

    private void setupButtons() {
        buttonSoundMap.keySet().forEach(button -> {
            button.setFont(FontLoader.medium(14));
            soundStates.put(button, false);
        });
    }

    @FXML
    private void toggleSound(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        Sound sound = buttonSoundMap.get(clickedButton);
        boolean isActive = soundStates.get(clickedButton);

        if (isActive) {
            clickedButton.getStyleClass().remove("active");
            audioManager.stopSound(sound);
        } else {
            clickedButton.getStyleClass().add("active");
            audioManager.playSound(sound, DEFAULT_VOLUME);
        }

        soundStates.put(clickedButton, !isActive);
    }

    public void cleanup() {
        if (audioManager != null) {
            audioManager.stopAll();
        }
    }
}
