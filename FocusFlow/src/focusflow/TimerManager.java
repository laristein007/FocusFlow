package focusflow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Manages the Pomodoro countdown timer for the FocusFlow application.
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
 * This class controls the timer logic used during a focus session. It starts,
 * pauses, and resets the Pomodoro countdown. It also notifies the user interface
 * whenever the timer changes and runs a completion action when the session ends.
 *
 * Main responsibilities:
 * - Store the default Pomodoro work time
 * - Start the countdown timer
 * - Pause the countdown timer
 * - Reset the timer back to the default time
 * - Report the remaining time to the user interface
 * - Run completion logic when a Pomodoro session finishes
 */
public class TimerManager {
    private static final int DEFAULT_WORK_SECONDS = 25 * 60;
    private int remainingSeconds = DEFAULT_WORK_SECONDS;
    private boolean running = false;
    private Timeline timeline;
    private java.util.function.IntConsumer onTick;
    private Runnable onComplete;

    public void startSession() {
        if (running) return;
        running = true;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                if (onTick != null) onTick.accept(remainingSeconds);
            } else {
                pauseSession();
                resetTimer();
                if (onComplete != null) onComplete.run();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void pauseSession() {
        running = false;
        if (timeline != null) timeline.pause();
    }

    public void resetTimer() {
        pauseSession();
        remainingSeconds = DEFAULT_WORK_SECONDS;
        if (onTick != null) onTick.accept(remainingSeconds);
    }

    public int getRemainingSeconds() { return remainingSeconds; }
    public boolean isRunning() { return running; }
    public void setOnTick(java.util.function.IntConsumer onTick) { this.onTick = onTick; }
    public void setOnComplete(Runnable onComplete) { this.onComplete = onComplete; }
}
