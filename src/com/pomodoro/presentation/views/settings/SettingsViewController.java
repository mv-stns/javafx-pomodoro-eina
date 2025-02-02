package com.pomodoro.presentation.views.settings;

import com.pomodoro.business.utils.Debug;
import com.pomodoro.business.utils.FontLoader;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SettingsViewController {

    @FXML
    private Button sessionButton, ambientButton;

    @FXML
    private VBox sessionSettingsView, ambientSettingsView;

    @FXML
    private AnchorPane settingsWrapper;

    @FXML
    public void initialize() {
        setStyling();
        sessionButton.getStyleClass().add("active");
        showSessionSettings();
    }

    @FXML
    private void showSessionSettings() {
        Platform.runLater(() -> {
            sessionSettingsView.setVisible(true);
            ambientSettingsView.setVisible(false);
            setActiveButton(sessionButton);
        });
    }

    @FXML
    private void showAmbientSettings() {
        Platform.runLater(() -> {
            sessionSettingsView.setVisible(false);
            ambientSettingsView.setVisible(true);
            setActiveButton(ambientButton);
        });
    }

    private void setStyling() {
        settingsWrapper.lookupAll(".nav-item").stream().forEach(node -> {
            ((Button) node).setFont(FontLoader.medium(14));
        });
    }

    private void setActiveButton(Button activeButton) {
        List.of(sessionButton, ambientButton).forEach(button -> button.getStyleClass().remove("active"));
        activeButton.getStyleClass().add("active");
    }
}
