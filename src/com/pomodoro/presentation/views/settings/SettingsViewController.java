package com.pomodoro.presentation.views.settings;

import com.pomodoro.business.utils.Debug;
import com.pomodoro.business.utils.FontLoader;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SettingsViewController {
    @FXML
    private Button sessionButton, ambientButton;

    @FXML
    private VBox sessionSettingsView, ambientSettingsView;

    @FXML
    private StackPane contentArea;

    @FXML
    private AnchorPane settingsWrapper;

    @FXML
    public void initialize() {

        if (sessionSettingsView != null && ambientSettingsView != null) {
            sessionSettingsView.setVisible(true);
            ambientSettingsView.setVisible(false);
        } else {
            System.err.println("ERROR: Views not properly initialized!");
        }


        if (sessionButton != null) {
            sessionButton.getStyleClass().add("active");
        }
    }

    @FXML
    private void showSessionSettings() {
        if (sessionSettingsView != null && ambientSettingsView != null) {
            sessionSettingsView.setVisible(true);
            ambientSettingsView.setVisible(false);
            setActiveButton(sessionButton);
        } else {
            System.err.println("ERROR: Cannot show session settings - views not initialized");
        }
    }

    @FXML
    private void showAmbientSettings() {
        if (sessionSettingsView != null && ambientSettingsView != null) {
            sessionSettingsView.setVisible(false);
            ambientSettingsView.setVisible(true);
            setActiveButton(ambientButton);
        } else {
            System.err.println("ERROR: Cannot show ambient settings - views not initialized");
        }
    }

    private void setActiveButton(Button activeButton) {
        if (sessionButton != null && ambientButton != null) {
            sessionButton.getStyleClass().remove("active");
            ambientButton.getStyleClass().remove("active");
            activeButton.getStyleClass().add("active");
        }
    }
}
