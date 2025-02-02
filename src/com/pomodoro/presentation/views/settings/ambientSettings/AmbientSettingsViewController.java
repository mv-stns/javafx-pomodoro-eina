package com.pomodoro.presentation.views.settings.ambientSettings;

import com.pomodoro.business.utils.Debug;
import com.pomodoro.business.utils.FontLoader;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;

public class AmbientSettingsViewController {

  @FXML
  private Button riverButton,
      wavesButton,
      campfireButton,
      windButton,
      howlingWindButton,
      treeWindButton,
      lightRainButton,
      heavyRainButton,
      thunderButton,
      rainOnWindowButton,
      rainOnUmbrellaButton,
      rainOnTentButton;

  @FXML
  private VBox box;

  private Map<Button, Boolean> soundStates = new HashMap<>();

  @FXML
  private void initialize() {
    Button[] buttons = {
        riverButton,
        wavesButton,
        campfireButton,
        windButton,
        howlingWindButton,
        treeWindButton,
        lightRainButton,
        heavyRainButton,
        thunderButton,
        rainOnWindowButton,
        rainOnUmbrellaButton,
        rainOnTentButton
    };

    for (Button button : buttons) {
      button.setFont(FontLoader.medium(14));
      soundStates.put(button, false);
    }
  }

  @FXML
  private void toggleSound(ActionEvent event) { // Füge ActionEvent als Parameter hinzu
    Button clickedButton = (Button) event.getSource();
    boolean isActive = soundStates.get(clickedButton);

    if (isActive) {
      clickedButton.getStyleClass().remove("active");
      // Stop sound
    } else {
      clickedButton.getStyleClass().add("active");
      // Play sound
    }

    soundStates.put(clickedButton, !isActive);
  }
}