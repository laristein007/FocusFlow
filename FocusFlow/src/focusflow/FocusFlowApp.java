package focusflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * FocusFlow Pomodoro App
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
 * FocusFlow is a productivity application that helps users stay focused using
 * the Pomodoro technique. The app includes a countdown timer, a notes section,
 * and a checklist so users can organize their work during focus sessions.
 *
 * Main features:
 * - Pomodoro timer for focus sessions
 * - Start, pause, and reset timer controls
 * - Notes area for writing session notes
 * - Checklist for adding and removing tasks
 * - Session tracking for completed Pomodoro sessions
 * - Notification popup when a session is complete
 *
 * This class is responsible for building the JavaFX user interface and connecting
 * the app's managers, including the timer, notes, tasks, session tracking, and
 * notifications.
 */ 
public class FocusFlowApp extends Application {
    private final TimerManager timerManager = new TimerManager();
    private final NotesManager notesManager = new NotesManager();
    private final TaskManager taskManager = new TaskManager();
    private final SessionTracker sessionTracker = new SessionTracker();
    private final NotificationManager notificationManager = new NotificationManager();

    private final Label timerLabel = new Label("25:00");
    private final Button startPauseButton = new Button("▶");

    @Override
    public void start(Stage stage) {
        // Displays the current timer mode, such as Pomodoro, Work, or Paused.
        Label modeLabel = new Label("Pomodoro");
        modeLabel.setStyle("""
                -fx-font-size: 82px;
                -fx-font-weight: bold;
                -fx-text-fill: #4b4b4f;
                """);

        timerLabel.setStyle("""
                -fx-font-size: 110px;
                -fx-font-weight: bold;
                -fx-text-fill: #f5f5f5;
                """);

        startPauseButton.setText("▶");
        startPauseButton.setStyle("""
                -fx-font-size: 52px;
                -fx-background-color: transparent;
                -fx-text-fill: #4dff7a;
                -fx-cursor: hand;
                """);

        Button resetButton = new Button("↻");
        resetButton.setStyle("""
                -fx-font-size: 64px;
                -fx-background-color: transparent;
                -fx-text-fill: #ff5252;
                -fx-cursor: hand;
                """);

        // The Save Notes button is hidden because notes are loaded automatically
        // and the button is not currently shown in the interface.
        Button saveNotesButton = new Button("Save Notes");
        saveNotesButton.setVisible(false);

        // Text area where the user can write or view notes.
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("lorem ipsum ..\nIdem 240");
        notesArea.setText(notesManager.loadNotes());
        notesArea.setPrefSize(180, 115);
        notesArea.setStyle("""
                -fx-control-inner-background: #50546c;
                -fx-background-color: #50546c;
                -fx-text-fill: #dddddd;
                -fx-font-size: 18px;
                -fx-font-family: "Consolas";
                -fx-background-radius: 3px;
                """);

        notesArea.textProperty().addListener((observable, oldValue, newValue) -> notesManager.saveNotes(newValue));

        // Input field used to type a new checklist item.
        TextField taskInput = new TextField();
        taskInput.setPromptText("Add checklist item");
        taskInput.setStyle("""
                -fx-background-color: #262422;
                -fx-text-fill: #f1ff72;
                -fx-prompt-text-fill: #666a80;
                -fx-font-size: 14px;
                """);

        // Button that adds the typed checklist item to the list.
        Button addTaskButton = new Button("+");
        addTaskButton.setStyle("""
                -fx-background-color: #f3ff6b;
                -fx-text-fill: #1c1c1c;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        // Button that deletes the selected or checked checklist item.
        Button deleteTaskButton = new Button("Delete");
        deleteTaskButton.setStyle("""
                -fx-background-color: #ff5252;
                -fx-text-fill: #ffffff;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        ListView<CheckBox> taskList = new ListView<>();
        taskList.setPrefSize(185, 220);
        taskList.setStyle("""
                -fx-control-inner-background: #262422;
                -fx-background-color: #262422;
                -fx-background-radius: 3px;
                -fx-border-color: transparent;
                """);

        List<String> savedTasks = taskManager.loadTasks();

        if (savedTasks.isEmpty()) {
            taskManager.addTask("item1");
            taskManager.addTask("item2");
            taskManager.addTask("item3");
            taskManager.addTask("subitem1");
            taskManager.addTask("subitem2");
            savedTasks = taskManager.getTasks();
        }

        for (String task : savedTasks) {
            CheckBox taskCheckBox = new CheckBox(task);
            taskCheckBox.setStyle("-fx-text-fill: #f1ff72; -fx-font-size: 18px;");
            taskList.getItems().add(taskCheckBox);
        }

        // Adds a new task when the user clicks the plus button.
        // The task is saved through TaskManager and displayed as a checkbox.
        addTaskButton.setOnAction(event -> {
            String taskText = taskInput.getText();

            if (taskText != null && !taskText.isBlank()) {
                String trimmedTask = taskText.trim();

                taskManager.addTask(trimmedTask);

                CheckBox newTask = new CheckBox(trimmedTask);
                newTask.setStyle("-fx-text-fill: #f1ff72; -fx-font-size: 18px;");

                taskList.getItems().add(newTask);
                taskInput.clear();
            }
        });

        // Allows the user to press Enter in the text field to add a task.
        taskInput.setOnAction(event -> addTaskButton.fire());

        // Deletes either the currently selected task or any checked tasks.
        deleteTaskButton.setOnAction(event -> {
            CheckBox selectedTask = taskList.getSelectionModel().getSelectedItem();

            if (selectedTask != null) {
                taskManager.deleteTask(selectedTask.getText().trim());
                taskList.getItems().remove(selectedTask);
            } else {
                taskList.getItems().removeIf(task -> {
                    if (task.isSelected()) {
                        taskManager.deleteTask(task.getText().trim());
                        return true;
                    }

                    return false;
                });
            }
        });

        Label statusLabel = new Label("Ready to focus");
        statusLabel.setVisible(false);

        // Updates the timer label every time the TimerManager reports a tick.
        timerManager.setOnTick(this::updateTimerLabel);

        // Runs when the Pomodoro session finishes.
        // Tracks the completed session, shows a notification,
        // resets the play button, and updates the status message.
        timerManager.setOnComplete(() -> {
            sessionTracker.trackSession();
            notificationManager.sendNotification("Session Complete", "Pomodoro session complete! Take a break.");

            startPauseButton.setText("▶");
            startPauseButton.setStyle("""
                    -fx-font-size: 52px;
                    -fx-background-color: transparent;
                    -fx-text-fill: #4dff7a;
                    -fx-cursor: hand;
                    """);

            modeLabel.setText("Pomodoro");
            statusLabel.setText("Session complete. Total sessions: " + sessionTracker.getCompletedSessions());
        });

        // Starts or pauses the timer depending on the current timer state.
        startPauseButton.setOnAction(e -> {
            if (timerManager.isRunning()) {
                timerManager.pauseSession();

                startPauseButton.setText("▶");
                startPauseButton.setStyle("""
                        -fx-font-size: 52px;
                        -fx-background-color: transparent;
                        -fx-text-fill: #4dff7a;
                        -fx-cursor: hand;
                        """);

                modeLabel.setText("Paused");
                statusLabel.setText("Timer paused");
            } else {
                timerManager.startSession();
                startPauseButton.setText("Ⅱ");
                startPauseButton.setStyle("""
                        -fx-font-size: 52px;
                        -fx-background-color: transparent;
                        -fx-text-fill: #f3ff6b;
                        -fx-cursor: hand;
                        """);
                modeLabel.setText("Work");
                statusLabel.setText("Focus session running");
            }
        });

        // Resets the timer back to the original Pomodoro time.
        resetButton.setOnAction(e -> {
            timerManager.resetTimer();
            updateTimerLabel(timerManager.getRemainingSeconds());
            startPauseButton.setText("▶");
            startPauseButton.setStyle("""
                    -fx-font-size: 52px;
                    -fx-background-color: transparent;
                    -fx-text-fill: #4dff7a;
                    -fx-cursor: hand;
                    """);
            modeLabel.setText("Pomodoro");
            statusLabel.setText("Timer reset");
        });

        // Section title for the timer area.
        Label titleLabel = new Label("FocusFlow Pomodoro");
        titleLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        // Section title for the notes area.
        Label notesLabel = new Label("Notes");
        notesLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        // Section title for the checklist area.
        Label checklistLabel = new Label("Checklist");
        checklistLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        // Layout containers that organize the timer, notes, and checklist sections.
        VBox timerBox = new VBox(15, titleLabel, timerLabel, startPauseButton, resetButton, statusLabel);
        VBox notesBox = new VBox(10, notesLabel, notesArea, saveNotesButton);
        HBox taskControls = new HBox(8, taskInput, addTaskButton);
        VBox taskBox = new VBox(10, checklistLabel, taskList, taskControls, deleteTaskButton);

        // Main horizontal layout that places all sections side by side.
        HBox root = new HBox(25, timerBox, notesBox, taskBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1e1e2f;");

        // Creates the window scene and displays the application.
        Scene scene = new Scene(root, 950, 520);
        stage.setTitle("FocusFlow Pomodoro App");
        stage.setScene(scene);
        stage.show();
    }

    // Converts the remaining seconds into MM:SS format and updates the timer label.
    private void updateTimerLabel(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Launches the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}
