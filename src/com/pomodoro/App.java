package com.pomodoro;

import com.pomodoro.presentation.utils.FontLoader;
import java.awt.Taskbar;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class App extends Application {
  private AnchorPane timerView, notesView;

  @Override
  public void init() throws IOException {
    FontLoader.loadFonts();
    loadFXMLViews();
  }

  @Override
  public void start(Stage mainStage) throws Exception {
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

    setAppIcon(mainStage);
    mainStage.setScene(scene);
    mainStage.setMinHeight(590);
    mainStage.setMinWidth(950);
    mainStage.setTitle("Tomodoro");
    mainStage.show();
  }

  private void loadFXMLViews() throws IOException {
    try {
      FXMLLoader timerLoader = loadFXML("com/pomodoro/presentation/views/timer/timerView.fxml");
      FXMLLoader notesLoader = loadFXML("com/pomodoro/presentation/views/notes/notesView.fxml");
      timerView = timerLoader.load();
      notesView = notesLoader.load();
    } catch (IOException e) {
      System.err.println("FXML Files not Found!");
      e.printStackTrace();
    }
  }

  private FXMLLoader loadFXML(String path) {
    return new FXMLLoader(getClass().getClassLoader().getResource(path));
  }

  private void setAppIcon(Stage mainStage) {
    Image appIcon = new Image(App.class.getResourceAsStream("assets/TomodoroApp.png"));
    mainStage.getIcons().add(appIcon);

    // @author: Pablo Gallardo
    // https://runmodule.com/2020/01/05/how-to-set-dock-icon-of-java-application/
    Taskbar taskbar = Taskbar.getTaskbar();
    java.awt.Image awtAppIcon = SwingFXUtils.fromFXImage(appIcon, null);
    taskbar.setIconImage(awtAppIcon);
  }

  public static void main(String[] args) {
    Application.launch(App.class, args);
  }
}
