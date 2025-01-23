package com.pomodoro.presentation.views.timer;

import com.pomodoro.presentation.components.LetterSpacedText;
import com.pomodoro.presentation.utils.FontLoader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TimerViewController {

  @FXML private Button focusButton;
  @FXML private Button shortBreakButton;
  @FXML private Button longBreakButton;
  @FXML private Arc timerArc;
  @FXML private Arc timerArcStatic;
  @FXML private LetterSpacedText timeLabel;
  @FXML private Label rangeLabel;
  @FXML private Button resetButton;
  @FXML private Button playButton;
  @FXML private Button skipButton;
  @FXML private HBox phaseWrapper;

  private Timeline timeline;
  private boolean isRunning = false;
  private double remainingMillis;
  private static final int FOCUS_TIME = 5; // 25 minutes in seconds
  private static final int MILLIS_PER_SECOND = 1000;

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
    Rectangle rec = new Rectangle();
    rec.setWidth(FOCUS_TIME);
    timerArc.setStrokeType(StrokeType.CENTERED);

    // Phase Button Styles
    List.of(focusButton, shortBreakButton, longBreakButton)
        .forEach(
            button -> {
              HBox hbox = (HBox) button.getGraphic();
              ((Text) hbox.getChildren().get(1)).setFont(FontLoader.semiBold(14.0));
            });
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
    remainingMillis = FOCUS_TIME * MILLIS_PER_SECOND;
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
    remainingMillis = FOCUS_TIME * MILLIS_PER_SECOND;
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
    // Update time label (still showing seconds precision)
    int totalSeconds = (int) (remainingMillis / MILLIS_PER_SECOND);
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;

    Platform.runLater(
        () -> {
          timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
        });

    // Smooth arc progress
    double progress = 1 - (remainingMillis / (FOCUS_TIME * MILLIS_PER_SECOND));
    Platform.runLater(
        () -> {
        //   timerArc.setStartAngle(90);
          timerArc.setLength(-progress*360); // Negative to go clockwise
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
