package com.pomodoro.presentation.views.timer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;

public class TimerViewController {

  @FXML private Button focusButton;
  @FXML private Button shortBreakButton;
  @FXML private Button longBreakButton;
  @FXML private Arc timerArc;
  @FXML private Arc timerArcStatic;
  @FXML private Label timeLabel;
  @FXML private Label rangeLabel;
  @FXML private Button resetButton;
  @FXML private Button playButton;
  @FXML private Button skipButton;
  @FXML private HBox phaseWrapper;

  private Timeline timeline;
  private boolean isRunning = false;
  private int remainingSeconds;
  private static final int FOCUS_TIME = 1 * 60; // 25 minutes in seconds

  @FXML
  public void initialize() {
    phaseWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
    phaseWrapper.setMaxWidth(Region.USE_PREF_SIZE);

    setupButtons();
    setupTimer();
    updateDisplay();
  }

  private void setupButtons() {
    playButton.setOnAction(e -> toggleTimer());
    resetButton.setOnAction(e -> resetTimer());
    skipButton.setOnAction(e -> skipTimer());

    focusButton.setOnAction(e -> switchMode("focus"));
    shortBreakButton.setOnAction(e -> switchMode("shortBreak"));
    longBreakButton.setOnAction(e -> switchMode("longBreak"));
  }

  private void setupTimer() {
    remainingSeconds = FOCUS_TIME;
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                e -> {
                  remainingSeconds--;
                  updateDisplay();
                  if (remainingSeconds <= 0) {
                    timeline.stop();
                    // Handle timer completion
                  }
                }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  private void toggleTimer() {
    if (isRunning) {
      timeline.pause();
      playButton.getStyleClass().remove("pause");
    } else {
      timeline.play();
      playButton.getStyleClass().add("pause");
    }
    isRunning = !isRunning;
  }

  private void resetTimer() {
    timeline.stop();
    remainingSeconds = FOCUS_TIME;
    isRunning = false;
    playButton.getStyleClass().remove("pause");
    updateDisplay();
  }

  private void skipTimer() {
    // Implement skip functionality
  }

  private void switchMode(String mode) {
    // Implement mode switching logic
  }

  private void updateDisplay() {
    // Update time label
    int minutes = remainingSeconds / 60;
    int seconds = remainingSeconds % 60;
    Platform.runLater(
        () -> {
          timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

    // Update arc progress - Simplified
    double progress = (double) (FOCUS_TIME - remainingSeconds) / FOCUS_TIME;
    Platform.runLater(
        () -> {
          timerArc.setStartAngle(90); // Keep it fixed at top
          timerArc.setLength(-progress * 360); // Negative to go clockwise
        });

    // Update range label
    LocalTime now = LocalTime.now();
    LocalTime endTime = now.plusSeconds(remainingSeconds);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    Platform.runLater(
        () -> {
          rangeLabel.setText(
              String.format("%s → %s", now.format(formatter), endTime.format(formatter)));
        });
  }
}
