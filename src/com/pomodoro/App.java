package com.pomodoro;

import com.pomodoro.business.utils.FontLoader;
import com.pomodoro.presentation.views.reflection.ReflectionViewController;
import com.pomodoro.presentation.views.sidebar.SidebarController;
import com.pomodoro.presentation.views.timer.TimerViewController;
import com.pomodoro.presentation.views.timer.TimerViewController.ViewSwitchCallback;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
  private AnchorPane timerView, notesView, reflectionView;
  private TimerViewController timerViewController;
  private VBox sidebarView;
  private StackPane contentArea;
  private Scene mainScene;

  @Override
  public void init() throws IOException {
    FontLoader.loadFonts();
    loadFXMLViews();
  }

  @Override
  public void start(Stage mainStage) throws Exception {
    List.of(timerView, notesView, reflectionView)
        .forEach(view -> HBox.setHgrow(view, Priority.ALWAYS));

    StackPane root = new StackPane();
    root.getStyleClass().add("root-container");

    contentArea = new StackPane();
    contentArea.getStyleClass().add("content-area");

    HBox mainViewContainer = new HBox();
    mainViewContainer.getChildren().addAll(timerView, notesView);
    mainViewContainer.setPrefWidth(Double.MAX_VALUE);

    contentArea.getChildren().add(mainViewContainer);

    AnchorPane sidebarContainer = new AnchorPane();

    sidebarContainer.getChildren().add(sidebarView);

    AnchorPane.setTopAnchor(sidebarView, 0.0);
    AnchorPane.setBottomAnchor(sidebarView, 0.0);
    AnchorPane.setLeftAnchor(sidebarView, 0.0);

    sidebarContainer.setPickOnBounds(false);

    contentArea.setStyle("-fx-padding: 0 0 0 60;");

    root.getChildren().addAll(contentArea, sidebarContainer);

    mainScene = new Scene(root, 1440, 800);

    loadStylesheets(mainScene);

    setAppIcon(mainStage);
    mainStage.setScene(mainScene);
    mainStage.setMinHeight(590);
    mainStage.setMinWidth(950);
    mainStage.setTitle("Tomodoro");
    mainStage.show();
  }

  private void loadFXMLViews() throws IOException {
    try {

      FXMLLoader sidebarLoader =
          loadFXML("com/pomodoro/presentation/views/sidebar/sidebarView.fxml");
      FXMLLoader timerLoader = loadFXML("com/pomodoro/presentation/views/timer/timerView.fxml");
      FXMLLoader notesLoader = loadFXML("com/pomodoro/presentation/views/notes/notesView.fxml");
      FXMLLoader reflectionLoader =
          loadFXML("com/pomodoro/presentation/views/reflection/reflectionView.fxml");

      sidebarView = sidebarLoader.load();
      SidebarController sidebarController = sidebarLoader.getController();

      sidebarController.setOnViewSwitch(
          viewType -> {
            switch (viewType) {
              case HOME -> showMainView();
              case STATISTICS -> showStatisticsView();
              case SETTINGS -> showSettingsView();
            }
          });

      timerView = timerLoader.load();
      notesView = notesLoader.load();
      reflectionView = reflectionLoader.load();
      timerViewController = timerLoader.getController();
      ReflectionViewController reflectionController = reflectionLoader.getController();
      reflectionController.setTimerController(timerViewController);

      ViewSwitchCallback viewSwitchCallback =
          new TimerViewController.ViewSwitchCallback() {
            @Override
            public void switchToReflection() {
              contentArea.getChildren().clear();
              contentArea.getChildren().add(reflectionView);
            }

            @Override
            public void switchToMain() {
              contentArea.getChildren().clear();
              HBox mainViewContainer = new HBox();
              mainViewContainer.getChildren().addAll(timerView, notesView);
              contentArea.getChildren().add(mainViewContainer);
            }
          };

      reflectionController.setOnSave(
          () -> {
            timerViewController.onReflectionSaved();
          });

      timerViewController.setViewSwitchCallback(viewSwitchCallback);
      reflectionController.setViewSwitchCallback(viewSwitchCallback);

    } catch (IOException e) {
      System.err.println("FXML Files not Found!");
      e.printStackTrace();
    }
  }

  private void loadStylesheets(Scene scene) {
    try {
      URL variablesUrl =
          getClass().getClassLoader().getResource("com/pomodoro/presentation/views/variables.css");
      URL sidebarStylesUrl =
          getClass()
              .getClassLoader()
              .getResource("com/pomodoro/presentation/views/sidebar/styles.css");

      if (variablesUrl == null) {
        throw new IllegalStateException("Could not find variables.css");
      }

      scene.getStylesheets().add(variablesUrl.toExternalForm());
      if (sidebarStylesUrl != null) {
        scene.getStylesheets().add(sidebarStylesUrl.toExternalForm());
      }
    } catch (Exception e) {
      System.err.println("Error loading stylesheets: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void showMainView() {
    HBox mainViewContainer = new HBox();
    mainViewContainer.getChildren().addAll(timerView, notesView);
    contentArea.getChildren().clear();
    contentArea.getChildren().add(mainViewContainer);
  }

  private void showStatisticsView() {
    contentArea.getChildren().clear();
  }

  private void showSettingsView() {
    contentArea.getChildren().clear();
  }

  private FXMLLoader loadFXML(String path) {
    return new FXMLLoader(getClass().getClassLoader().getResource(path));
  }

  private void setAppIcon(Stage mainStage) {
    Image appIcon = new Image(App.class.getResourceAsStream("assets/TomodoroApp.png"));
    mainStage.getIcons().add(appIcon);

    Taskbar taskbar = Taskbar.getTaskbar();
    java.awt.Image awtAppIcon = SwingFXUtils.fromFXImage(appIcon, null);
    taskbar.setIconImage(awtAppIcon);
  }

  public static void main(String[] args) {
    Application.launch(App.class, args);
  }
}
