package focusflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Manages the notes feature for the FocusFlow application.
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
 * This class is responsible for saving and loading the user's notes.
 * Notes are stored in a text file so they remain available after the
 * application is closed and reopened.
 *
 * Main responsibilities:
 * - Save notes entered by the user
 * - Load saved notes when the application starts
 * - Create the notes file if it does not already exist
 * - Handle file read and write errors safely
 */
public class NotesManager {
    private final Path notesFile = Path.of("focusflow_notes.txt");

    /**
     * Saves the user's notes to the notes file.
     *
     * If the notes value is null, an empty string is saved instead. The file is
     * created if it does not already exist, and existing file contents are replaced
     * with the newest notes text.
     *
     * @param notes the notes text entered by the user
     */
    public void saveNotes(String notes) {
        try {
            Files.writeString(notesFile, notes == null ? "" : notes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Unable to save notes: " + e.getMessage());
        }
    }

    /**
     * Loads the user's saved notes from the notes file.
     *
     * If the notes file exists, its contents are returned as a string. If the file
     * does not exist or cannot be read, an empty string is returned.
     *
     * @return the saved notes text, or an empty string if no notes are available
     */
    public String loadNotes() {
        try {
            if (Files.exists(notesFile)) {
                return Files.readString(notesFile);
            }
        } catch (IOException e) {
            System.err.println("Unable to load notes: " + e.getMessage());
        }
        return "";
    }
}
