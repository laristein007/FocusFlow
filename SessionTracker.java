package focusflow;

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
}
