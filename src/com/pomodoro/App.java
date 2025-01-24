package com.pomodoro;

import com.pomodoro.presentation.utils.FontLoader;
import java.net.URL;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class App extends Application {
  private AnchorPane timerView, notesView;

  @Override
  public void init() {
    FontLoader.loadFonts();
  }

  @Override
  public void start(Stage mainStage) throws Exception {
    FXMLLoader timerLoader =
        new FXMLLoader(
            getClass()
                .getClassLoader()
                .getResource("com/pomodoro/presentation/views/timer/timerView.fxml"));

    FXMLLoader notesLoader =
        new FXMLLoader(
            getClass()
                .getClassLoader()
                .getResource("com/pomodoro/presentation/views/notes/notesView.fxml"));

    timerView = timerLoader.load();
    notesView = notesLoader.load();
    List.of(timerView, notesView).forEach(view -> HBox.setHgrow(view, Priority.ALWAYS));

    HBox root = new HBox();
    root.getChildren().addAll(timerView, notesView);
    root.setPrefWidth(Double.MAX_VALUE);

    Scene scene = new Scene(root, 1440, 800);

    try {
      URL variablesUrl =
          getClass().getClassLoader().getResource("com/pomodoro/presentation/views/variables.css");
      if (variablesUrl == null) {
        throw new IllegalStateException("Could not find variables.css");
      }
      scene.getStylesheets().add(variablesUrl.toExternalForm());

    } catch (Exception e) {
      System.err.println("Error loading variables.css: " + e.getMessage());
      e.printStackTrace();
    }

    mainStage.setScene(scene);
    mainStage.setMinHeight(590);
    mainStage.setMinWidth(950);
    mainStage.setTitle("Pomodoro App");
    mainStage.show();
  }

  public static void main(String[] args) {
    Application.launch(App.class, args);
  }
}
