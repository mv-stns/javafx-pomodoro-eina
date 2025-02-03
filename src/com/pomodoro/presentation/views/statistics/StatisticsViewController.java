package com.pomodoro.presentation.views.statistics;

import com.pomodoro.business.Session;
import com.pomodoro.business.utils.StatisticsAnalyzer;
import com.pomodoro.business.utils.DailyStats;
import com.pomodoro.business.utils.DataManager;
import com.pomodoro.business.utils.FontLoader;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StatisticsViewController {

    @FXML
    private Label todaySessionsLabel;
    @FXML
    private Label totalFocusTimeLabel;
    @FXML
    private Label completionRateLabel;

    @FXML
    private BarChart<String, Number> weeklyChart;
    @FXML
    private PieChart categoryChart;

    @FXML
    private TableView<DailyStats> detailsTable;
    @FXML
    private TableColumn<DailyStats, String> dateColumn;
    @FXML
    private TableColumn<DailyStats, Integer> sessionsColumn;
    @FXML
    private TableColumn<DailyStats, Long> focusTimeColumn;
    @FXML
    private TableColumn<DailyStats, Double> completionColumn;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    public void initialize() {
        setupFonts();
        weeklyChart.setAnimated(false);
        categoryChart.setAnimated(false);
        loadStatistics();
        setupTable();
        updateCharts();
    }

    public void refreshData() {
        loadStatistics();
        updateCharts();
    }

    private void setupFonts() {
        todaySessionsLabel.setFont(FontLoader.semiBold(32));
        totalFocusTimeLabel.setFont(FontLoader.semiBold(32));
        completionRateLabel.setFont(FontLoader.semiBold(32));
    }

    private void loadStatistics() {
        List<DailyStats> weekStats = StatisticsAnalyzer.analyzePastWeek();
        DailyStats todayStats = weekStats.get(weekStats.size() - 1);


        todaySessionsLabel.setText(String.valueOf(todayStats.getCompletedSessions()));

        long weeklyFocusHours = weekStats.stream()
                .mapToLong(stats -> stats.getTotalFocusMinutes())
                .sum() / 60;
        totalFocusTimeLabel.setText(weeklyFocusHours + "h");

        double avgCompletionRate = weekStats.stream()
                .mapToDouble(stats -> stats.getCompletionRate())
                .average()
                .orElse(0.0);
        completionRateLabel.setText(String.format("%.1f%%", avgCompletionRate));

        detailsTable.setItems(FXCollections.observableArrayList(weekStats));
    }

    private void setupTable() {
        dateColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(dateFormatter)));

        sessionsColumn.setCellValueFactory(
                cellData -> new SimpleIntegerProperty(cellData.getValue().getCompletedSessions()).asObject());

        focusTimeColumn.setCellValueFactory(
                cellData -> new SimpleLongProperty(cellData.getValue().getTotalFocusMinutes()).asObject());

        completionColumn.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getCompletionRate()).asObject());

        completionColumn.setCellFactory(column -> new TableCell<DailyStats, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", item));
                }
            }
        });
    }

    private void updateCharts() {

        List<DailyStats> weekStats = StatisticsAnalyzer.analyzePastWeek();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (DailyStats stats : weekStats) {
            series.getData().add(new XYChart.Data<>(
                    stats.getDate().format(DateTimeFormatter.ofPattern("EE")),
                    stats.getCompletedSessions()));
        }


        weeklyChart.getData().clear();
        weeklyChart.getData().add(series);


        Map<String, Integer> categoryStats = StatisticsAnalyzer.getMostUsedCategories(
                DataManager.loadTodaysSessions());

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryStats.forEach((category, count) -> pieChartData.add(new PieChart.Data(category, count)));


        categoryChart.setData(pieChartData);
    }
}
