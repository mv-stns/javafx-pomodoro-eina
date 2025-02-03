package com.pomodoro.presentation.views.timer;

import com.pomodoro.business.PomoPhase;
import com.pomodoro.business.Session;
import com.pomodoro.business.audio.AudioManager;
import com.pomodoro.business.audio.Sound;
import com.pomodoro.business.config.AppConfig;
import com.pomodoro.business.utils.DataManager;
import com.pomodoro.business.utils.FontLoader;
import com.pomodoro.presentation.components.LetterSpacedText;
import de.hsrm.mi.eibo.simpleplayer.SimpleAudioPlayer;
import de.hsrm.mi.eibo.simpleplayer.SimpleMinim;
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
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TimerViewController {

  @FXML
  private Button focusButton, shortBreakButton, longBreakButton;
  @FXML
  private Arc timerArc, timerArcStatic;
  @FXML
  private LetterSpacedText timeLabel;
  @FXML
  private Label rangeLabel;
  @FXML
  private Button resetButton, playButton, skipButton;
  @FXML
  private HBox phaseWrapper;

  private Timeline timeline;
  private boolean isRunning = false;
  private double remainingMillis;
  private static final int MILLIS_PER_SECOND = 1000;
  private PomoPhase currentPhase = PomoPhase.FOCUS;
  private static final int POMODOROS_UNTIL_LONG_BREAK = 3;
  private int completedPomodoros = 0;
  private boolean isInBreak = false;
  private ViewSwitchCallback viewSwitchCallback;
  private Runnable onReflectionComplete;
  private Session currentSession;
  private AudioManager audioManager;
  private Sound pauseSound;
  private SimpleAudioPlayer pausePlayer;

  public interface ViewSwitchCallback {
    void switchToReflection();

    void switchToMain();
  }

  public void setViewSwitchCallback(ViewSwitchCallback callback) {
    this.viewSwitchCallback = callback;
  }

  @FXML
  public void initialize() {
    setupStyles();
    setupButtons();
    setupTimer();
    updateDisplay();
    setupAudio();

    // Listener für Änderungen der Dauer
    AppConfig.focusDurationProperty().addListener((obs, oldVal, newVal) -> {
      if (currentPhase == PomoPhase.FOCUS) {
        onConfigChanged();
      }
    });

    AppConfig.shortBreakDurationProperty().addListener((obs, oldVal, newVal) -> {
      if (currentPhase == PomoPhase.SHORT_BREAK) {
        onConfigChanged();
      }
    });

    AppConfig.longBreakDurationProperty().addListener((obs, oldVal, newVal) -> {
      if (currentPhase == PomoPhase.LONG_BREAK) {
        onConfigChanged();
      }
    });
  }

  private void setupAudio() {
    audioManager = new AudioManager();
    SimpleMinim minim = new SimpleMinim();
    pausePlayer = minim.loadMP3File("src/resources/audio/Pause.mp3");
  }

  private void setupStyles() {
    phaseWrapper.setPrefWidth(Region.USE_COMPUTED_SIZE);
    phaseWrapper.setMaxWidth(Region.USE_PREF_SIZE);

    timeLabel.setText("00:00");
    timeLabel.setLetterSpacing(-2);
    timeLabel.setFont(FontLoader.semiBold(48));
    timeLabel.setFill(Color.WHITE);
    timeLabel.setAlignment(Pos.CENTER);

    timerArc.setStrokeLineCap(StrokeLineCap.ROUND);
    timerArcStatic.setStrokeLineCap(StrokeLineCap.ROUND);
    timerArc.setStrokeType(StrokeType.CENTERED);

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

  private void startNewSession() {
    currentSession = new Session(currentPhase);
  }

  private void completeSession() {
    if (currentSession != null) {
      currentSession.complete();
      DataManager.saveSession(currentSession);
    }
  }

  private void interruptSession(String reason) {
    if (currentSession != null) {
      currentSession.interrupt(reason);
      DataManager.saveSession(currentSession);
    }
  }

  private void setupTimer() {
    // Hole die aktuelle Dauer aus AppConfig basierend auf der Phase
    int durationInSeconds = switch (currentPhase) {
      case FOCUS -> AppConfig.FOCUS_DURATION;
      case SHORT_BREAK -> AppConfig.SHORT_BREAK_DURATION;
      case LONG_BREAK -> AppConfig.LONG_BREAK_DURATION;
    };

    remainingMillis = durationInSeconds * MILLIS_PER_SECOND;
    timeline = new Timeline(
        new KeyFrame(
            Duration.millis(16),
            e -> {
              remainingMillis -= 16;
              updateDisplay();
              if (remainingMillis <= 0) {
                timeline.stop();
                handlePhaseCompletion();
              }
            }));
    timeline.setCycleCount(Timeline.INDEFINITE);
  }

  private void handlePhaseCompletion() {
    isRunning = false;
    playButton.getStyleClass().remove("pause");

    if (currentSession != null) {
      completeSession();
    }

    if (currentPhase == PomoPhase.FOCUS) {
      // Spiele den Pausensound ab, wenn der Fokus-Timer endet
      playPauseSound();

      if (viewSwitchCallback != null) {
        viewSwitchCallback.switchToReflection();
        onReflectionComplete = () -> {
          completedPomodoros++;
          if (completedPomodoros >= POMODOROS_UNTIL_LONG_BREAK) {
            switchMode("longBreak");
            completedPomodoros = 0;
          } else {
            switchMode("shortBreak");
          }
          startTimer();
        };
      }
    } else {
      switchMode("focus");
      startTimer();
    }
  }

  private void startTimer() {
    Platform.runLater(
        () -> {
          remainingMillis = currentPhase.getDurationInSeconds() * MILLIS_PER_SECOND;
          updateDisplay();
          if (!isRunning) {
            toggleTimer();
          }
        });
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
    remainingMillis = AppConfig.FOCUS_DURATION * MILLIS_PER_SECOND;
    isRunning = false;
    playButton.getStyleClass().remove("pause");
    completedPomodoros = 0;
    currentPhase = PomoPhase.FOCUS;
    updatePhaseButtons();
    updateDisplay();
    updateTimerArc();
  }

  private void skipTimer() {
    timeline.stop();
    if (currentSession != null) {
      interruptSession("Manually skipped");
    }

    if (currentPhase == PomoPhase.FOCUS) {
      handlePhaseCompletion();
    } else {
      switchMode("focus");
      startTimer();
    }
  }

  private void switchMode(String mode) {
    boolean wasInFocus = currentPhase == PomoPhase.FOCUS;

    switch (mode) {
      case "focus":
        currentPhase = PomoPhase.FOCUS;
        remainingMillis = AppConfig.FOCUS_DURATION * MILLIS_PER_SECOND;
        break;
      case "shortBreak":
        currentPhase = PomoPhase.SHORT_BREAK;
        remainingMillis = AppConfig.SHORT_BREAK_DURATION * MILLIS_PER_SECOND;
        // if (wasInFocus) {
        //   playPauseSound();
        // }
        break;
      case "longBreak":
        currentPhase = PomoPhase.LONG_BREAK;
        remainingMillis = AppConfig.LONG_BREAK_DURATION * MILLIS_PER_SECOND;
        // if (wasInFocus) {
        //   playPauseSound();
        // }
        break;
    }

    updatePhaseButtons();
    updateDisplay();
  }

  private void playPauseSound() {
    if (pausePlayer != null) {
      new Thread() {
        public void run() {
          pausePlayer.rewind();
          pausePlayer.play();
        }
      }.start();
    }
  }

  private void updatePhaseButtons() {
    Platform.runLater(
        () -> {
          focusButton.getStyleClass().remove("selected");
          shortBreakButton.getStyleClass().remove("selected");
          longBreakButton.getStyleClass().remove("selected");
          System.out.println(currentPhase);

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
        });
  }

  private void updateTimerArc() {
    // Berechne die Gesamtdauer in Millisekunden für die aktuelle Phase
    double totalDuration = switch (currentPhase) {
      case FOCUS -> AppConfig.FOCUS_DURATION * MILLIS_PER_SECOND;
      case SHORT_BREAK -> AppConfig.SHORT_BREAK_DURATION * MILLIS_PER_SECOND;
      case LONG_BREAK -> AppConfig.LONG_BREAK_DURATION * MILLIS_PER_SECOND;
    };

    // Berechne den Fortschritt basierend auf der neuen Gesamtdauer
    double progress = 1 - (remainingMillis / totalDuration);

    Platform.runLater(() -> {
      timerArc.setLength(-progress * 360);
    });
  }

  private void updateDisplay() {
    int totalSeconds = (int) (remainingMillis / MILLIS_PER_SECOND);
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;

    Platform.runLater(() -> {
      timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
      updateTimerArc(); // Aktualisiere den Arc bei jedem Display-Update
    });

    LocalTime now = LocalTime.now();
    LocalTime endTime = now.plusSeconds(totalSeconds);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    Platform.runLater(() -> {
      rangeLabel.setText(
          String.format("%s → %s", now.format(formatter), endTime.format(formatter)));
    });
  }

  public void onConfigChanged() {
    if (!isRunning) {
      // Aktualisiere remainingMillis basierend auf der neuen Konfiguration
      remainingMillis = switch (currentPhase) {
        case FOCUS -> AppConfig.FOCUS_DURATION * MILLIS_PER_SECOND;
        case SHORT_BREAK -> AppConfig.SHORT_BREAK_DURATION * MILLIS_PER_SECOND;
        case LONG_BREAK -> AppConfig.LONG_BREAK_DURATION * MILLIS_PER_SECOND;
      };

      // Aktualisiere Display und Arc
      updateDisplay();
      updateTimerArc();
    }
  }

  public void onReflectionSaved() {
    if (onReflectionComplete != null) {
      onReflectionComplete.run();
    }
    if (viewSwitchCallback != null) {
      viewSwitchCallback.switchToMain();
    }
  }
}
