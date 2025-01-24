package com.pomodoro.presentation.views.notes;

import com.pomodoro.business.Note;
import com.pomodoro.business.Task;
import com.pomodoro.business.utils.*;
import com.pomodoro.presentation.components.LetterSpacedText;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public class NotesViewController {
  @FXML private Label notesLabel;
  @FXML private Label tasksLabel;
  @FXML private Button addTaskButton, deleteButton;
  @FXML private TextArea notesArea;
  @FXML private TextField taskInput;
  @FXML private VBox taskList;
  @FXML private HBox charContainer;
  @FXML private Text characterCount;
  private static final String NOTE_FILE = "tomodoro.note.txt";
  private Note currentNote;
  private ObservableList<Task> tasks = FXCollections.observableArrayList();
  private static final int MAX_CHARS = 500;

  @FXML
  private void initialize() {
    setupStyles();
    setupNotesArea();
    setupTaskInput();
    loadTodaysData();
  }

  private void loadTodaysData() {
    // Load notes
    String savedNotes = DataManager.loadTodaysNotes();
    if (!savedNotes.isEmpty()) {
      notesArea.setText(savedNotes);
      currentNote = new Note(savedNotes);
    }

    // Load tasks
    String savedTasks = DataManager.loadTodaysTasks();
    if (!savedTasks.isEmpty()) {
      for (String line : savedTasks.split("\n")) {
        String[] parts = line.split("\t");
        if (parts.length >= 2) {
          Task task = new Task(parts[0]);
          if (Boolean.parseBoolean(parts[1])) {
            task.setFinished();
          }
          tasks.add(task);
          addTaskToView(task);
        }
      }
    }
  }

  private void setupStyles() {
    // Debug.debugNode(charContainer.getParent());
    List.of(notesLabel, tasksLabel)
        .forEach(
            label -> {
              label.setFont(FontLoader.medium(12.0));
              label.setPrefWidth(Double.MAX_VALUE);
            });

    characterCount.setFont(FontLoader.regular(12.0));
    characterCount.setFill(Color.web("#6F7785"));
    addTaskButton.setFont(FontLoader.medium(20.0));
    addTaskButton.setTextFill(Color.web("#FF4E02"));
    notesArea.setFont(FontLoader.regular(14.0));
    taskInput.setFont(FontLoader.regular(14.0));
  }

  private void setupNotesArea() {
    notesArea
        .textProperty()
        .addListener(
            (obs, old, newValue) -> {
              if (newValue.length() > MAX_CHARS) {
                notesArea.setText(old);
                return;
              }
              characterCount.setText(newValue.length() + "/" + MAX_CHARS);
              saveNote(newValue);
            });
  }

  private void saveNote(String noteText) {
    currentNote = new Note(noteText);
    DataManager.saveTodaysNotes(noteText);
  }

  private void saveTasks() {
    StringBuilder sb = new StringBuilder();
    for (Task task : tasks) {
      sb.append(task.getTaskName()).append("\t").append(task.getCompleted()).append("\n");
    }
    DataManager.saveTodaysTasks(sb.toString());
  }

  private void setupTaskInput() {
    taskInput
        .textProperty()
        .addListener(
            (obs, old, newValue) -> {
              addTaskButton.setDisable(newValue.trim().isEmpty());
              addTaskButton.setOnAction(
                  e -> {
                    String taskText = taskInput.getText().trim();
                    if (!taskText.isEmpty()) {
                      addTask();
                    }
                  });
            });
    taskInput.setOnAction(e -> addTask());
  }

  @FXML
  private void addTask() {
    String taskText = taskInput.getText().trim();
    if (!taskText.isEmpty()) {
      Task newTask = new Task(taskText);
      tasks.add(newTask);
      addTaskToView(newTask);
      taskInput.clear();
    }
    saveTasks();
  }

  private void addTaskToView(Task task) {
    HBox taskContainer = new HBox();
    taskContainer.getStyleClass().add("task-item");
    taskContainer.setSpacing(8);

    // Checkbox
    CheckBox checkbox = new CheckBox();
    checkbox.setSelected(task.getCompleted());
    checkbox.setOnAction(e -> toggleTaskCompletion(task, taskContainer));

    // Task text
    LetterSpacedText taskLabel = new LetterSpacedText(task.getTaskName());
    taskLabel.setFont(FontLoader.semiBold(14.0));
    taskLabel.setLetterSpacing(-0.6);
    taskLabel.setFill(Color.WHITE);
    taskLabel.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(taskLabel, Priority.ALWAYS);

    // Delete button
    deleteButton = new Button();
    deleteButton.getStyleClass().add("delete-task-button");
    SVGPath deletePath = new SVGPath();
    deletePath.setContent(
        "M5.82251 4.83439L10.2775 0.379395L11.5501 1.65199L7.09511 6.10699L11.5501 10.562L10.2775"
            + " 11.8346L5.82251 7.37959L1.36751 11.8346L0.0949097 10.562L4.54991 6.10699L0.0949097"
            + " 1.65199L1.36751 0.379395L5.82251 4.83439Z");
    deletePath.setMouseTransparent(true);
    deletePath.getStyleClass().add("delete-task-icon");
    deleteButton.setGraphic(deletePath);
    deleteButton.setOnAction(e -> deleteTask(task, taskContainer));

    taskContainer.getChildren().addAll(checkbox, taskLabel, deleteButton);
    if (task.getCompleted()) {
      taskContainer.getStyleClass().add("completed-task");
    }
    taskList.getChildren().add(taskContainer);
  }

  private void toggleTaskCompletion(Task task, HBox taskContainer) {
    task.setFinished();
    if (task.getCompleted()) {
      taskContainer.getStyleClass().add("completed-task");
    } else {
      taskContainer.getStyleClass().remove("completed-task");
    }
    saveTasks();
  }

  private void deleteTask(Task task, HBox taskContainer) {
    tasks.remove(task);
    taskList.getChildren().remove(taskContainer);
    saveTasks();
  }

  // Getters for persistence
  public Note getCurrentNote() {
    return currentNote;
  }

  public ObservableList<Task> getTasks() {
    return tasks;
  }
}
