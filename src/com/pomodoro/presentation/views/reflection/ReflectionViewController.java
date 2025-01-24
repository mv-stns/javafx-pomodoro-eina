package com.pomodoro.presentation.views.reflection;

import com.pomodoro.business.Category;
import com.pomodoro.business.Note;
import com.pomodoro.business.PomoPhase;
import com.pomodoro.business.Session;
import com.pomodoro.business.SessionManager;
import com.pomodoro.business.utils.DataManager;
import com.pomodoro.business.utils.FontLoader;
import com.pomodoro.presentation.components.LetterSpacedText;
import com.pomodoro.presentation.views.timer.TimerViewController;
import com.pomodoro.presentation.views.timer.TimerViewController.ViewSwitchCallback;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class ReflectionViewController {
  @FXML private LetterSpacedText titleText, catHeader, saveButtonText;
  @FXML private Text subtitleText;
  @FXML private HBox moodSelector;
  @FXML private ComboBox<Category> categoryComboBox;
  @FXML private FlowPane categoryChips;
  @FXML private TextArea reflectionArea;
  @FXML private Text characterCount;
  @FXML private Button saveButton;
  @FXML private Button closeButton;

  private ObservableList<Category> categories = FXCollections.observableArrayList();
  private ObservableList<Category> selectedCategories = FXCollections.observableArrayList();
  private final SessionManager sessionManager = SessionManager.getInstance();
  private String selectedMood = null;
  private static final int MAX_CHARS = 500;
  private Runnable onSaveCallback;
  private ViewSwitchCallback viewSwitchCallback;
  private TimerViewController timerController;

  @FXML
  private void initialize() {
    setupStyles();
    setupMoodSelector();
    setupCategoryComboBox();
    setupReflectionArea();
    sessionManager
        .currentNoteProperty()
        .addListener(
            (obs, oldNote, newNote) -> {
              if (newNote != null) {
                reflectionArea.setText(newNote.getNoteData());
              }
            });
    Note currentNote = sessionManager.getCurrentNote();
    if (currentNote != null) {
      reflectionArea.setText(currentNote.getNoteData());
    }
  }

  private void setupStyles() {
    titleText.setFont(FontLoader.bold(36));
    titleText.setLetterSpacing(-3);
    titleText.setAlignment(Pos.CENTER);
    subtitleText.setFont(FontLoader.regular(16));
    subtitleText.setFill(Color.web("#FFFFFF", 0.5));

    saveButton.setFont(FontLoader.medium(14));
    closeButton.setFont(FontLoader.medium(14));

    reflectionArea.setFont(FontLoader.regular(14));
    characterCount.setFont(FontLoader.regular(12));
    characterCount.setFill(Color.web("#6F7785"));

    
    categories.addAll(
        new Category("ENIA", false), new Category("Sem3", false), new Category("Java", false));
    categoryComboBox.setItems(categories);
    catHeader.setFont(FontLoader.semiBold(14));
    catHeader.setLetterSpacing(-0.6);

    saveButton.setPrefHeight(Region.USE_COMPUTED_SIZE);
    saveButton.setMaxHeight(40);
    saveButton.setPrefWidth(Region.USE_COMPUTED_SIZE);
    saveButtonText.setFont(FontLoader.semiBold(14));
    saveButtonText.setLetterSpacing(-0.6);
    saveButtonText.setAlignment(Pos.CENTER);
    moodSelector
        .getChildren()
        .forEach(
            node -> {
              ((Button) node).setPrefWidth(500 / 3);
            });
  }

  @FXML
  private void selectMood(ActionEvent event) {
    Button clickedButton = (Button) event.getSource();
    Text moodText = (Text) clickedButton.getGraphic();

    
    moodSelector.getChildren().forEach(node -> node.getStyleClass().remove("selected"));

    
    clickedButton.getStyleClass().add("selected");

    
    selectedMood = moodText.getText();
    System.out.println("Selected mood: " + selectedMood);
  }

  private void setupMoodSelector() {
    moodSelector
        .getChildren()
        .forEach(
            node -> {
              Button button = (Button) node;
              button.setOnAction(
                  e -> {
                    moodSelector.getChildren().forEach(b -> b.getStyleClass().remove("selected"));
                    button.getStyleClass().add("selected");
                    selectedMood = ((Text) button.getGraphic()).getText();
                  });
            });
  }

  private void setupCategoryComboBox() {
    categoryComboBox.setItems(categories);

    
    categoryComboBox.setConverter(
        new StringConverter<Category>() {
          @Override
          public String toString(Category category) {
            return category != null ? category.getName() : "";
          }

          @Override
          public Category fromString(String string) {
            if (string == null || string.trim().isEmpty()) return null;
            return new Category(string.trim());
          }
        });

    
    categoryComboBox
        .getEditor()
        .setOnAction(
            e -> {
              String text = categoryComboBox.getEditor().getText().trim();
              if (!text.isEmpty()) {
                if (isCategoryAlreadySelected(text)) {
                  showError("Diese Kategorie wurde bereits hinzugefügt!");
                  categoryComboBox.getEditor().clear();
                  return;
                }

                
                Category newCategory = new Category(text);
                Category existing =
                    categories.stream()
                        .filter(c -> c.getName().equalsIgnoreCase(text))
                        .findFirst()
                        .orElse(null);

                if (existing == null) {
                  categories.add(newCategory);
                  selectedCategories.add(newCategory);
                  addCategoryChip(newCategory);
                } else {
                  selectedCategories.add(existing);
                  addCategoryChip(existing);
                }

                categoryComboBox.getEditor().clear();
              }
            });

    
    categoryComboBox.setOnAction(
        e -> {
          Category selected = categoryComboBox.getValue();
          if (selected != null) {
            if (isCategoryAlreadySelected(selected.getName())) {
              showError("Diese Kategorie wurde bereits hinzugefügt!");
            } else {
              selectedCategories.add(selected);
              addCategoryChip(selected);
            }
            Platform.runLater(
                () -> {
                  categoryComboBox.getSelectionModel().clearSelection();
                  categoryComboBox.getEditor().clear();
                });
          }
        });

    
    categoryComboBox
        .getEditor()
        .focusedProperty()
        .addListener(
            (obs, wasFocused, isFocused) -> {
              if (!isFocused) {
                Platform.runLater(
                    () -> {
                      categoryComboBox.getEditor().clear();
                      categoryComboBox.getSelectionModel().clearSelection();
                    });
              }
            });
  }

  private void addCategoryChip(Category category) {
    HBox chip = new HBox();
    chip.getStyleClass().add("category-chip");
    chip.setAlignment(Pos.CENTER);

    Label nameLabel = new Label(category.getName());
    nameLabel.setFont(FontLoader.medium(12));
    nameLabel.setTextFill(Color.WHITE);

    if (category.isRemovable()) {
      Button removeButton = new Button("×");
      removeButton.getStyleClass().add("remove-category");
      removeButton.setOnAction(
          e -> {
            selectedCategories.remove(category);
            categoryChips.getChildren().remove(chip);
          });
      chip.getChildren().addAll(nameLabel, removeButton);
    } else {
      chip.getChildren().add(nameLabel);
    }

    categoryChips.getChildren().add(chip);
  }

  private void setupReflectionArea() {
    reflectionArea
        .textProperty()
        .addListener(
            (obs, old, newValue) -> {
              if (newValue.length() > MAX_CHARS) {
                reflectionArea.setText(old);
                return;
              }
              characterCount.setText(newValue.length() + "/" + MAX_CHARS);
            });
  }

  @FXML
  private void saveReflection() {
    if (selectedMood == null
        || selectedCategories.isEmpty()
        || reflectionArea.getText().trim().isEmpty()) {
      showError("Bitte fülle alle Felder aus!");
      return;
    }

    
    Session session = new Session(PomoPhase.FOCUS);
    session.setMood(selectedMood);
    session.setNotes(reflectionArea.getText());
    selectedCategories.forEach(session::addCategory);

    
    sessionManager.setCurrentSession(session);
    sessionManager.setCurrentNote(new Note(reflectionArea.getText()));

    
    DataManager.saveSession(session);

    
    if (onSaveCallback != null) {
      onSaveCallback.run();
    }

    closeDialog();
  }

  public void setViewSwitchCallback(ViewSwitchCallback callback) {
    this.viewSwitchCallback = callback;
  }

  @FXML
  private void closeDialog() {
    if (viewSwitchCallback != null) {
      viewSwitchCallback.switchToMain();
    }
  }

  
  public String getSelectedMood() {
    return selectedMood;
  }

  public ObservableList<Category> getSelectedCategories() {
    return selectedCategories;
  }

  public String getReflectionText() {
    return reflectionArea.getText();
  }

  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Fehler");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert
        .getDialogPane()
        .getStylesheets()
        .add(getClass().getResource("styles.css").toExternalForm());
    alert.showAndWait();
  }

  private boolean isCategoryAlreadySelected(String categoryName) {
    return selectedCategories.stream()
        .anyMatch(cat -> cat.getName().equalsIgnoreCase(categoryName.trim()));
  }

  public void setOnSave(Runnable callback) {
    this.onSaveCallback = callback;
  }

  public void setTimerController(TimerViewController controller) {
    this.timerController = controller;
  }
}
