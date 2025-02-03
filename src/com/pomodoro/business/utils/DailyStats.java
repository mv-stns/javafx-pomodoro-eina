package com.pomodoro.business.utils;

import com.pomodoro.business.Category;
import com.pomodoro.business.Session;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyStats {
    private final LocalDate date;
    private final int completedSessions;
    private final int interruptedSessions;
    private final long totalFocusMinutes;
    private final Map<String, Integer> categoryFrequency;
    private final double completionRate;

    public DailyStats(LocalDate date, List<Session> sessions) {
        this.date = date;
        this.completedSessions = (int) sessions.stream()
                .filter(session -> session.isCompleted())
                .count();
        this.interruptedSessions = (int) sessions.stream()
                .filter(session -> !session.isCompleted())
                .count();
        this.totalFocusMinutes = sessions.stream()
                .mapToLong(session -> session.getActualDurationSeconds())
                .sum() / 60;
        this.categoryFrequency = calculateCategoryFrequency(sessions);
        this.completionRate = sessions.isEmpty() ? 0 : (double) completedSessions / sessions.size() * 100;
    }

    private Map<String, Integer> calculateCategoryFrequency(List<Session> sessions) {
        Map<String, Integer> frequency = new HashMap<>();
        for (Session session : sessions) {
            for (Category category : session.getCategories()) {
                String name = category.getName();
                frequency.put(name, frequency.getOrDefault(name, 0) + 1);
            }
        }
        return frequency;
    }


    public LocalDate getDate() {
        return date;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public int getInterruptedSessions() {
        return interruptedSessions;
    }

    public long getTotalFocusMinutes() {
        return totalFocusMinutes;
    }

    public Map<String, Integer> getCategoryFrequency() {
        return new HashMap<>(categoryFrequency);
    }

    public double getCompletionRate() {
        return completionRate;
    }
}
