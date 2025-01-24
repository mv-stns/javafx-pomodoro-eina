package com.pomodoro.presentation.views.timer;

import com.pomodoro.business.PomoPhase;
import com.pomodoro.business.utils.FontLoader;
import com.pomodoro.presentation.components.LetterSpacedText;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TimerViewController {

  @FXML private Button focusButton, shortBreakButton, longBreakButton;
  @FXML private Arc timerArc, timerArcStatic;
  @FXML private LetterSpacedText timeLabel;
  @FXML private Label rangeLabel;
  @FXML private Button resetButton, playButton, skipButton;
  @FXML private HBox phaseWrapper;

  private Timeline timeline;
  private boolean isRunning = false;
  private double remainingMillis;
  private static final int FOCUS_TIME = 5; // 25 minutes in seconds
  private static final int MILLIS_PER_SECOND = 1000;
  private PomoPhase currentPhase = PomoPhase.FOCUS;

  @FXML
  public void initialize() {
    setupStyles();
    setupButtons();
    setupTimer();
    updateDisplay();
  }

  private void setupStyles() {
    phaseWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
    phaseWrapper.setMaxWidth(Region.USE_PREF_SIZE);

    // Timer Label Styles
    timeLabel.setText("00:00");
    timeLabel.setLetterSpacing(-2);
    timeLabel.setFont(FontLoader.semiBold(48));
    timeLabel.setFill(Color.WHITE);
    timeLabel.setAlignment(Pos.CENTER);

    // Arc Styles
    timerArc.setStrokeLineCap(StrokeLineCap.ROUND);
    timerArcStatic.setStrokeLineCap(StrokeLineCap.ROUND);
    timerArc.setStrokeType(StrokeType.CENTERED);

    // Phase Button Styles
    List.of(focusButton, shortBreakButton, longBreakButton)
        .forEach(
            button -> {
              HBox hbox = (HBox) button.getGraphic();
              ((Text) hbox.getChildren().get(1)).setFont(FontLoader.semiBold(14.0));
            });

    rangeLabel.setFont(FontLoader.regular(16));

    
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
    remainingMillis = currentPhase.getDurationInSeconds() * MILLIS_PER_SECOND;
    timeline =
        new Timeline(
            new KeyFrame(
                Duration.millis(16), // ca. 60 FPS (1000ms / 60 ≈ 16.67ms)
                e -> {
                  remainingMillis -= 16;
                  updateDisplay();
                  if (remainingMillis <= 0) {
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
    remainingMillis = currentPhase.getDurationInSeconds() * MILLIS_PER_SECOND;
    isRunning = false;
    playButton.getStyleClass().remove("pause");
    updateDisplay();
  }

  private void skipTimer() {
    // Implement skip functionality
  }

  private void switchMode(String mode) {
    switch (mode) {
      case "focus":
        currentPhase = PomoPhase.FOCUS;
        break;
      case "shortBreak":
        currentPhase = PomoPhase.SHORT_BREAK;
        break;
      case "longBreak":
        currentPhase = PomoPhase.LONG_BREAK;
        break;
    }

    // Reset timer with new duration
    remainingMillis = currentPhase.getDurationInSeconds() * MILLIS_PER_SECOND;
    resetTimer();

    // Update UI to reflect new phase
    updatePhaseButtons();
  }

  private void updatePhaseButtons() {
    // Remove selected class from all buttons
    focusButton.getStyleClass().remove("selected");
    shortBreakButton.getStyleClass().remove("selected");
    longBreakButton.getStyleClass().remove("selected");

    // Add selected class to current phase button
    switch (currentPhase) {
      case FOCUS:
        focusButton.getStyleClass().add("selected");
        break;
      case SHORT_BREAK:
        shortBreakButton.getStyleClass().add("selected");
        break;
      case LONG_BREAK:
        longBreakButton.getStyleClass().add("selected");
        break;
    }
  }

  private void updateDisplay() {
    // Update time label (still showing seconds precision)
    int totalSeconds = (int) (remainingMillis / MILLIS_PER_SECOND);
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;

    Platform.runLater(
        () -> {
          timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

    // Smooth arc progress
    double progress = 1 - (remainingMillis / (currentPhase.getDurationInSeconds() * MILLIS_PER_SECOND));
    Platform.runLater(
        () -> {
          //   timerArc.setStartAngle(90);
          timerArc.setLength(-progress * 360); // Negative to go clockwise
        });

    // Update range label
    LocalTime now = LocalTime.now();
    LocalTime endTime = now.plusSeconds(totalSeconds);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    Platform.runLater(
        () -> {
          rangeLabel.setText(
              String.format("%s → %s", now.format(formatter), endTime.format(formatter)));
        });
  }
}
