package focusflow;

import javafx.scene.control.Alert;

/**
 * Manages user notifications for the FocusFlow application.
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
 * This class is responsible for displaying popup messages to the user.
 * It uses JavaFX alert dialogs to show important application messages, such as
 * when a Pomodoro session has been completed.
 *
 * Main responsibilities:
 * - Create notification popup windows
 * - Display notification titles and messages
 * - Inform the user when an important event occurs
 */
public class NotificationManager {
    /**
     * Displays an informational popup notification to the user.
     *
     * The notification includes a title and message. The popup waits for the user
     * to close it before continuing.
     *
     * @param title the title shown at the top of the notification window
     * @param message the message shown inside the notification body
     */
    public void sendNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
