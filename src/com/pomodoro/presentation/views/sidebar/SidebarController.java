package com.pomodoro.presentation.views.sidebar;

import com.pomodoro.presentation.views.ViewType;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController {

  @FXML private VBox sidebar;

  @FXML private Button homeButton;

  @FXML private Button statisticsButton;

  @FXML private Button settingsButton;

  private Consumer<ViewType> onViewSwitch;

  @FXML
  private void initialize() {
    setActiveButton(homeButton);
  }

  public void setOnViewSwitch(Consumer<ViewType> onViewSwitch) {
    this.onViewSwitch = onViewSwitch;
  }

  @FXML
  private void onHomeClicked() {
    setActiveButton(homeButton);
    if (onViewSwitch != null) {
      onViewSwitch.accept(ViewType.HOME);
    }
  }

  @FXML
  private void onStatisticsClicked() {
    setActiveButton(statisticsButton);
    if (onViewSwitch != null) {
      onViewSwitch.accept(ViewType.STATISTICS);
    }
  }

  @FXML
  private void onSettingsClicked() {
    setActiveButton(settingsButton);
    if (onViewSwitch != null) {
      onViewSwitch.accept(ViewType.SETTINGS);
    }
  }

  private void setActiveButton(Button activeButton) {
    homeButton.getStyleClass().remove("active");
    statisticsButton.getStyleClass().remove("active");
    settingsButton.getStyleClass().remove("active");
    activeButton.getStyleClass().add("active");
  }
}
