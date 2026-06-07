package focusflow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
