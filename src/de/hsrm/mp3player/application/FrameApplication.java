package de.hsrm.mp3player.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FrameApplication extends Application {

    public void init() {
        // Implementation eventueller Business-Logiken
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 1440, 800);
        
        Text text = new Text("Hello JavaFX");
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 36px;");
        text.setX(50);
        text.setY(50);

        root.getChildren().add(text);

        mainStage.setScene(scene);
        mainStage.setTitle("MP3 Player");
        mainStage.show();
    }

    public void stop() {
        // Freigabe von Resourcen
    }
}
