package com.pomodoro.presentation.views.settings.sessionSettings;

import com.pomodoro.business.config.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Button;

public class SessionSettingsViewController {

    @FXML
    private Spinner<Integer> focusDurationSpinner;

    @FXML
    private Spinner<Integer> shortBreakDurationSpinner;

    @FXML
    private Spinner<Integer> longBreakDurationSpinner;

    @FXML
    private Button saveButton;

    @FXML
    private Button resetButton;

    @FXML
    public void initialize() {
        setupSpinner(focusDurationSpinner, 1, 60, AppConfig.FOCUS_DURATION / (AppConfig.DEBUG_MODE ? 1 : 60));
        setupSpinner(shortBreakDurationSpinner, 1, 30,
                AppConfig.SHORT_BREAK_DURATION / (AppConfig.DEBUG_MODE ? 1 : 60));
        setupSpinner(longBreakDurationSpinner, 1, 60, AppConfig.LONG_BREAK_DURATION / (AppConfig.DEBUG_MODE ? 1 : 60));
    }

    private void setupSpinner(Spinner<Integer> spinner, int min, int max, int initialValue) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max,
                initialValue);
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
    }

    @FXML
    private void handleSave() {
        AppConfig.setFocusDuration(focusDurationSpinner.getValue());
        AppConfig.setShortBreakDuration(shortBreakDurationSpinner.getValue());
        AppConfig.setLongBreakDuration(longBreakDurationSpinner.getValue());
        // Hier könnte noch eine Erfolgsmeldung oder Animation hinzugefügt werden
    }

    @FXML
    private void handleReset() {
        AppConfig.resetToDefaults();
        initialize(); // Spinner auf Standardwerte zurücksetzen
    }
}
