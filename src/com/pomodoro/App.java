package com.pomodoro;

import com.pomodoro.presentation.views.timer.TimerViewController;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class App extends Application {
  private AnchorPane timerView;

  @Override
  public void start(Stage mainStage) throws Exception {
    FXMLLoader loader =
        new FXMLLoader(
            getClass()
                .getClassLoader()
                .getResource("com/pomodoro/presentation/views/timer/timerView.fxml"));
    timerView = loader.load();

    Scene scene = new Scene(timerView, 1440, 800);

    // Add only variables.css to the scene
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
    mainStage.setTitle("Pomodoro App");
    mainStage.show();
  }

  public static void main(String[] args) {
    Application.launch(App.class, args);
  }
}
