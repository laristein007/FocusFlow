package focusflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
 */
public class FocusFlowApp extends Application {
    private final TimerManager timerManager = new TimerManager();
    private final NotesManager notesManager = new NotesManager();
    private final TaskManager taskManager = new TaskManager();
    private final SessionTracker sessionTracker = new SessionTracker();
    private final NotificationManager notificationManager = new NotificationManager();
    private final PlaylistManager playlistManager = new PlaylistManager();

    private final Label timerLabel = new Label("25:00");
    private final Button startPauseButton = new Button("▶");

    private MediaPlayer mediaPlayer;
    private final ListView<File> playlistView = new ListView<>();

    @Override
    public void start(Stage stage) {
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

        Button saveNotesButton = new Button("Save Notes");
        saveNotesButton.setVisible(false);

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

        TextField taskInput = new TextField();
        taskInput.setPromptText("Add checklist item");
        taskInput.setStyle("""
                -fx-background-color: #262422;
                -fx-text-fill: #f1ff72;
                -fx-prompt-text-fill: #666a80;
                -fx-font-size: 14px;
                """);

        Button addTaskButton = new Button("+");
        addTaskButton.setStyle("""
                -fx-background-color: #f3ff6b;
                -fx-text-fill: #1c1c1c;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

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

        taskInput.setOnAction(event -> addTaskButton.fire());

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

        timerManager.setOnTick(this::updateTimerLabel);

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

        startPauseButton.setOnAction(event -> {
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

        resetButton.setOnAction(event -> {
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

        Label titleLabel = new Label("FocusFlow Pomodoro");
        titleLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        Label notesLabel = new Label("Notes");
        notesLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        Label checklistLabel = new Label("Checklist");
        checklistLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        Label musicLabel = new Label("Music Player");
        musicLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        playlistView.setPrefSize(210, 220);
        playlistView.setStyle("""
                -fx-control-inner-background: #262422;
                -fx-background-color: #262422;
                -fx-background-radius: 3px;
                -fx-border-color: transparent;
                -fx-text-fill: #dddddd;
                """);

        playlistView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);

                if (empty || file == null) {
                    setText(null);
                } else {
                    setText(file.getName());
                    setStyle("-fx-text-fill: #f1ff72; -fx-font-size: 14px;");
                }
            }
        });

        playlistView.getItems().addAll(playlistManager.loadPlaylist());

        Button addSongButton = new Button("Add Song");
        addSongButton.setStyle("""
                -fx-background-color: #f3ff6b;
                -fx-text-fill: #1c1c1c;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        Button removeSongButton = new Button("Remove");
        removeSongButton.setStyle("""
                -fx-background-color: #ff5252;
                -fx-text-fill: #ffffff;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        Button playPauseSongButton = new Button("▶");
        playPauseSongButton.setStyle("""
                -fx-font-size: 24px;
                -fx-background-color: transparent;
                -fx-text-fill: #4dff7a;
                -fx-cursor: hand;
                """);

        Button stopSongButton = new Button("■");
        stopSongButton.setStyle("""
                -fx-font-size: 24px;
                -fx-background-color: transparent;
                -fx-text-fill: #ff5252;
                -fx-cursor: hand;
                """);

        Button nextSongButton = new Button("Next");
        nextSongButton.setStyle("""
                -fx-background-color: #50546c;
                -fx-text-fill: #ffffff;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        addSongButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Add Music Files");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a", "*.aac")
            );

            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                playlistView.getItems().addAll(selectedFiles);
                playlistManager.savePlaylist(playlistView.getItems());
            }
        });

        removeSongButton.setOnAction(event -> {
            File selectedSong = playlistView.getSelectionModel().getSelectedItem();

            if (selectedSong != null) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                }

                playlistView.getItems().remove(selectedSong);
                playPauseSongButton.setText("▶");
            }
        });

        playPauseSongButton.setOnAction(event -> {
            File selectedSong = playlistView.getSelectionModel().getSelectedItem();

            if (selectedSong == null) {
                return;
            }

            if (mediaPlayer == null || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                playSelectedSong(playPauseSongButton);
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPauseSongButton.setText("▶");
            } else {
                mediaPlayer.play();
                playPauseSongButton.setText("Ⅱ");
            }
        });

        stopSongButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                playPauseSongButton.setText("▶");
            }
        });

        nextSongButton.setOnAction(event -> {
            if (playlistView.getItems().isEmpty()) {
                return;
            }

            int currentIndex = playlistView.getSelectionModel().getSelectedIndex();
            int nextIndex = currentIndex + 1;

            if (nextIndex >= playlistView.getItems().size()) {
                nextIndex = 0;
            }

            playlistView.getSelectionModel().select(nextIndex);
            playSelectedSong(playPauseSongButton);
        });

        playlistView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                playSelectedSong(playPauseSongButton);
            }
        });

        VBox timerBox = new VBox(15, titleLabel, timerLabel, startPauseButton, resetButton, statusLabel);
        VBox notesBox = new VBox(10, notesLabel, notesArea, saveNotesButton);
        HBox taskControls = new HBox(8, taskInput, addTaskButton);
        VBox taskBox = new VBox(10, checklistLabel, taskList, taskControls, deleteTaskButton);
        HBox musicControls = new HBox(8, playPauseSongButton, stopSongButton, nextSongButton);
        HBox playlistEditControls = new HBox(8, addSongButton, removeSongButton);
        VBox musicBox = new VBox(10, musicLabel, playlistView, musicControls, playlistEditControls);

        HBox root = new HBox(25, timerBox, notesBox, taskBox, musicBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1e1e2f;");

        Scene scene = new Scene(root, 1190, 520);
        stage.setTitle("FocusFlow Pomodoro App");
        stage.setScene(scene);
        stage.show();
    }

    private void updateTimerLabel(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void playSelectedSong(Button playPauseSongButton) {
        File selectedSong = playlistView.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(selectedSong.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnEndOfMedia(() -> {
            if (!playlistView.getItems().isEmpty()) {
                int currentIndex = playlistView.getSelectionModel().getSelectedIndex();
                int nextIndex = currentIndex + 1;

                if (nextIndex >= playlistView.getItems().size()) {
                    nextIndex = 0;
                }

                playlistView.getSelectionModel().select(nextIndex);
                playSelectedSong(playPauseSongButton);
            }
        });

        mediaPlayer.play();
        playPauseSongButton.setText("Ⅱ");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
