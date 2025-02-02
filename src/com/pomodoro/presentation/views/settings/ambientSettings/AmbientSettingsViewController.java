package com.pomodoro.presentation.views.settings.ambientSettings;

import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

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
      soundStates.put(button, false);
    }
  }

  @FXML
  private void toggleSound() {
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
