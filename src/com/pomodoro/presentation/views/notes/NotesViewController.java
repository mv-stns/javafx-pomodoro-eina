package com.pomodoro.presentation.views.notes;

import com.pomodoro.presentation.utils.*;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NotesViewController {
  @FXML private Label notesLabel;
  @FXML private Label tasksLabel;
//   @FXML private Label characterCount;
  @FXML private Button addTaskButton;
  @FXML private TextArea notesArea;
  @FXML private TextField taskInput;
  @FXML private VBox taskList;
  @FXML private HBox charContainer;
  @FXML private Text characterCount;

  @FXML
  private void initialize() {
    setupStyles();
    setupNotesArea();
    setupTaskInput();
  }

  private void setupStyles() {
    // Debug.debugNode(charContainer.getParent());
    // Style all labels
    List.of(notesLabel, tasksLabel)
        .forEach(
            label -> {
              label.setFont(FontLoader.medium(12.0));
              label.setPrefWidth(Double.MAX_VALUE);
            });

    // Specific styles
    characterCount.setFont(FontLoader.regular(12.0));
    characterCount.setFill(Color.web("#6F7785"));

    addTaskButton.setFont(FontLoader.medium(20.0));
    addTaskButton.setTextFill(Color.web("#FF4E02")); // Orange color

    // TextArea and TextField styles
    notesArea.setFont(FontLoader.regular(14.0));
    taskInput.setFont(FontLoader.regular(14.0));
  }

  private void setupNotesArea() {
    notesArea
        .textProperty()
        .addListener(
            (obs, old, newValue) -> {
              int count = newValue.length();
              characterCount.setText(count + "/500");
            });
  }

  private void setupTaskInput() {
    // Task input handling will be implemented later
  }

  @FXML
  private void addTask() {
    // Task adding logic will be implemented later
  }
}
