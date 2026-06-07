package focusflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FocusFlowApp extends Application {
    private final TimerManager timerManager = new TimerManager();
    private final NotesManager notesManager = new NotesManager();
    private final TaskManager taskManager = new TaskManager();
    private final SessionTracker sessionTracker = new SessionTracker();
    private final NotificationManager notificationManager = new NotificationManager();
    private final APIManager apiManager = new APIManager();

    private final Label timerLabel = new Label("25:00");
    private final Button startPauseButton = new Button("Start");

    @Override
    public void start(Stage stage) {
        timerLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        Button resetButton = new Button("Reset");
        Button saveNotesButton = new Button("Save Notes");
        Button addTaskButton = new Button("Add Task");
        Button deleteTaskButton = new Button("Delete Task");

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Write notes here...");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter task");

        ListView<CheckBox> taskList = new ListView<>();
        Label statusLabel = new Label("Ready to focus");
        statusLabel.setStyle("-fx-text-fill: white;");

        timerManager.setOnTick(this::updateTimerLabel);
        timerManager.setOnComplete(() -> {
            sessionTracker.trackSession();
            notificationManager.sendNotification("Session Complete", "Pomodoro session complete! Take a break.");
            startPauseButton.setText("Start");
            statusLabel.setText("Session complete. Total sessions: " + sessionTracker.getCompletedSessions());
        });

        startPauseButton.setOnAction(e -> {
            if (timerManager.isRunning()) {
                timerManager.pauseSession();
                startPauseButton.setText("Start");
                statusLabel.setText("Timer paused");
            } else {
                timerManager.startSession();
                startPauseButton.setText("Pause");
                statusLabel.setText("Focus session running");
            }
        });

        resetButton.setOnAction(e -> {
            timerManager.resetTimer();
            updateTimerLabel(timerManager.getRemainingSeconds());
            startPauseButton.setText("Start");
            statusLabel.setText("Timer reset");
        });

        saveNotesButton.setOnAction(e -> {
            notesManager.saveNotes(notesArea.getText());
            statusLabel.setText("Notes saved");
        });

        addTaskButton.setOnAction(e -> {
            String task = taskInput.getText().trim();
            if (!task.isEmpty()) {
                taskManager.addTask(task);
                taskList.getItems().add(new CheckBox(task));
                taskInput.clear();
                statusLabel.setText("Task added");
            }
        });

        deleteTaskButton.setOnAction(e -> {
            CheckBox selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskManager.deleteTask(selected.getText());
                taskList.getItems().remove(selected);
                statusLabel.setText("Task deleted");
            }
        });

        VBox timerBox = new VBox(15, new Label("FocusFlow Pomodoro"), timerLabel, startPauseButton, resetButton, statusLabel);
        VBox notesBox = new VBox(10, new Label("Notes"), notesArea, saveNotesButton);
        VBox taskBox = new VBox(10, new Label("Checklist"), taskInput, addTaskButton, deleteTaskButton, taskList);

        HBox root = new HBox(25, timerBox, notesBox, taskBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1e1e2f;");

        Scene scene = new Scene(root, 950, 520);
        stage.setTitle("FocusFlow Pomodoro App");
        stage.setScene(scene);
        stage.show();
    }

    private void updateTimerLabel(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
