package focusflow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks completed Pomodoro focus sessions for the FocusFlow application.
 *
 * UMGC CMSC 495 Summer 2026
 * Laris Ndamo – Project Manager/UI/UX Designer
 * Robert Dayton – Frontend Developer
 * Dion Hatchett – Backend/API Developer
 * Tyson Howery – Frontend Developer
 * Chris McCourt – Tester/Documentation Specialist
 *
 * Version 1.0
 *
 * This class keeps count of completed focus sessions and stores the date and
 * time when each session is completed. The session history can be used to show
 * user progress and calculate how many Pomodoro sessions were completed today.
 *
 * Main responsibilities:
 * - Count completed Pomodoro sessions
 * - Store session completion times
 * - Return the total number of completed sessions
 * - Return a copy of the session history
 * - Count how many sessions were completed on the current day
 */
public class SessionTracker {
    private int completedSessions = 0;
    private final List<LocalDateTime> sessionHistory = new ArrayList<>();

    public void trackSession() {
        completedSessions++;
        sessionHistory.add(LocalDateTime.now());
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public List<LocalDateTime> getSessionHistory() {
        return new ArrayList<>(sessionHistory);
    }

    public int getSessionsCompletedToday() {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (LocalDateTime session : sessionHistory) {
            if (session.toLocalDate().equals(today)) {
                count++;
            }
        }

        return count;
    }

}
