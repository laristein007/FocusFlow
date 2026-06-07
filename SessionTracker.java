package focusflow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
