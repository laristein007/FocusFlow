package focusflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
    //Added for album cover
    private javafx.scene.image.ImageView albumArtView = new javafx.scene.image.ImageView();
    @Override
    public void start(Stage stage) {
        Label modeLabel = new Label("Pomodoro");
        
        //*****************************************************//
        //Added -- Laris
        //Set ID for CSS
        modeLabel.setId("modeLabel");
        timerLabel.setId("timerLabel");
        //Load fonts
        try {
            //Digitalio Font
            var digitalioStream = getClass().getResourceAsStream("/fonts/round-digitalio.ttf");
            if (digitalioStream != null) {
                Font.loadFont(digitalioStream, 100);
            }
            
            //Humaroid Font
            var humaroidStream = getClass().getResourceAsStream("/fonts/humaroid.otf");
            if (humaroidStream != null) {
                Font.loadFont(humaroidStream, 100);
            }
            
            //Code New Roman Font
            var codeNewRomanStream = getClass().getResourceAsStream("/fonts/code-new-roman.otf");
            if (codeNewRomanStream != null) {
                Font.loadFont(codeNewRomanStream, 100);
            }
            
        } catch (Exception e) {
            System.out.println("Error loading fonts stream:");
            e.printStackTrace();
        }
        //*****************************************************//
        
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
        notesArea.setPrefSize(350, 200);
        //Set notes to wrap properly in set area --Laris
        notesArea.setWrapText(true);
        
        notesArea.setStyle("""
                -fx-control-inner-background: #50546c;
                -fx-background-color: #50546c;
                -fx-text-fill: #dddddd;
                -fx-font-size: 14px;
                -fx-font-family: "Monospace";
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
                -fx-font-family: "Monospace";
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
            taskCheckBox.setStyle("-fx-text-fill: #f1ff72; -fx-font-size: 14px;");
            taskList.getItems().add(taskCheckBox);
        }

        addTaskButton.setOnAction(event -> {
            String taskText = taskInput.getText();

            if (taskText != null && !taskText.isBlank()) {
                String trimmedTask = taskText.trim();
                taskManager.addTask(trimmedTask);

                CheckBox newTask = new CheckBox(trimmedTask);
                newTask.setStyle("-fx-text-fill: #f1ff72; -fx-font-size: 14px;");

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
        //Commented out --Laris
        /* Label titleLabel = new Label("FocusFlow Pomodoro");
        titleLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """); */

        Label notesLabel = new Label("Notes");
        notesLabel.setId("notesLabel");
        /* notesLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """); */

        Label checklistLabel = new Label("Checklist");
        checklistLabel.setId("checklistLabel");
        /*checklistLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """); */

        Label musicLabel = new Label("Music Player");
        musicLabel.setId("musicLabel");
        /*musicLabel.setStyle("""
                -fx-text-fill: #f3ff6b;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """); */
        
        
        playlistView.setPrefSize(210, 220);
        playlistView.setStyle("""
                -fx-control-inner-background: #262422;
                -fx-background-color: #262422;
                -fx-background-radius: 3px;
                -fx-border-color: transparent;
                -fx-text-fill: #dddddd;
                """);
    //****************************************************************************//
        //Added album cover -- Laris
        albumArtView = new javafx.scene.image.ImageView();
        albumArtView.setFitWidth(210);
        albumArtView.setFitHeight(150);
        albumArtView.setPreserveRatio(true);
        
        
        
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

        Button playPauseSongButton = new Button("▷");
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

        Button nextSongButton = new Button("▷|"); //Updated the icon --Laris
        nextSongButton.setStyle("""
                -fx-font-size: 24px;
                -fx-background-color: transparent;
                -fx-text-fill: #8BE9FD;
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
                playPauseSongButton.setText("▷");
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
                playPauseSongButton.setText("▷");
            } else {
                mediaPlayer.play();
                playPauseSongButton.setText("Ⅱ");
            }
        });

        stopSongButton.setOnAction(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                playPauseSongButton.setText("▷");
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

        //VBox timerBox = new VBox(15, titleLabel, timerLabel, startPauseButton, resetButton, statusLabel);
        VBox notesBox = new VBox(10, notesLabel, notesArea, saveNotesButton);
        HBox taskControls = new HBox(8, taskInput, addTaskButton);
        VBox taskBox = new VBox(10, checklistLabel, taskList, taskControls, deleteTaskButton);
        //Stack checklist and notes to look like mockup --Laris
        VBox checklistAndNotesColumn = new VBox(25, taskBox, notesBox);
        
        //HBox musicControls = new HBox(8, playPauseSongButton, stopSongButton, nextSongButton);
        HBox musicControls = new HBox(12, playPauseSongButton, stopSongButton, nextSongButton);
        musicControls.setAlignment(javafx.geometry.Pos.CENTER);
        HBox playlistEditControls = new HBox(8, addSongButton, removeSongButton);
        //VBox musicBox = new VBox(10, musicLabel, playlistView, musicControls, playlistEditControls);
        VBox musicBox = new VBox(12, musicLabel, albumArtView, playlistView, musicControls, playlistEditControls);
        musicBox.setAlignment(javafx.geometry.Pos.CENTER);

        //Clean up UI: Adjusted button layout, display modeLabel --Laris
        //Group the buttons horizontally and center them
        HBox timerControls = new HBox(20, startPauseButton, resetButton);
        timerControls.setAlignment(javafx.geometry.Pos.CENTER);
        //Include modeLabel inside the vertical box list
        VBox timerBox = new VBox(15, modeLabel, timerLabel, timerControls, statusLabel);
        timerBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        HBox root = new HBox(25, timerBox, checklistAndNotesColumn, musicBox);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #1e1e1eff;");

        Scene scene = new Scene(root, 1190, 520);
        //Added stylesheet --Laris
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css")).toExternalForm());
        
        stage.setTitle("FocusFlow");
        stage.setScene(scene);
        //Set max width --Laris
        stage.setMaxWidth(1100);
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
        
        // Safely clear old player resources before creating a new one
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        
        // Initialize media stream
        Media media = new Media(selectedSong.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        
        // Track metadata additions -- Laris
        media.getMetadata().addListener((javafx.collections.MapChangeListener<String, Object>) change -> {
            if (change.wasAdded() && "image".equals(change.getKey())) {
                javafx.scene.image.Image albumArt = (javafx.scene.image.Image) change.getValueAdded();
                javafx.application.Platform.runLater(() -> albumArtView.setImage(albumArt));
            }
        });
        
        // Reset image if the song doesn't have any artwork
        mediaPlayer.setOnReady(() -> {
            if (!media.getMetadata().containsKey("image")) {
                javafx.application.Platform.runLater(() -> {
                    try {
                        var placeholderStream = getClass().getResourceAsStream("/focusflow/resources/blank.png");
                        if (placeholderStream != null) {
                            albumArtView.setImage(new javafx.scene.image.Image(placeholderStream));
                        } else {
                            albumArtView.setImage(null);
                        }
                    } catch (Exception e) {
                        albumArtView.setImage(null);
                    }
                });
            }
        });
        
        // Set up next track track automation safely
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
