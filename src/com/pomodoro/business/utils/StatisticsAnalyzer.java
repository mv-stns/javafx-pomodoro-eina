package com.pomodoro.business.utils;

import com.pomodoro.business.Session;
import com.pomodoro.business.PomoPhase;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsAnalyzer {

    public static List<DailyStats> analyzePastWeek() {
        LocalDate today = LocalDate.now();
        List<DailyStats> weekStats = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            List<Session> sessions = DataManager.loadSessionsForDate(date);
            weekStats.add(new DailyStats(date, sessions));
        }

        return weekStats;
    }

    public static Map<String, Integer> getMostUsedCategories(List<Session> sessions) {
        return sessions.stream()
                .flatMap(session -> session.getCategories().stream())
                .collect(Collectors.groupingBy(
                        category -> category.getName(),
                        Collectors.collectingAndThen(Collectors.counting(), count -> count.intValue())));
    }

    public static double getAverageCompletionRate(List<Session> sessions) {
        if (sessions.isEmpty())
            return 0;
        long completed = sessions.stream()
                .filter(session -> session.isCompleted())
                .count();
        return (double) completed / sessions.size() * 100;
    }

    public static Map<PomoPhase, Integer> getPhaseDistribution(List<Session> sessions) {
        Map<PomoPhase, Integer> distribution = new HashMap<>();
        for (Session session : sessions) {
            PomoPhase phase = session.getPhase();
            distribution.put(phase, distribution.getOrDefault(phase, 0) + 1);
        }
        return distribution;
    }

    public static int getTotalFocusTime(List<Session> sessions) {
        int totalSeconds = 0;
        for (Session session : sessions) {
            if (session.getPhase() == PomoPhase.FOCUS) {
                totalSeconds += session.getActualDurationSeconds();
            }
        }
        return totalSeconds / 60;
    }

    public static List<Session> getCompletedSessions(List<Session> sessions) {
        List<Session> completed = new ArrayList<>();
        for (Session session : sessions) {
            if (session.isCompleted()) {
                completed.add(session);
            }
        }
        return completed;
    }
}
