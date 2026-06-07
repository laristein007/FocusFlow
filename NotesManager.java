package focusflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class NotesManager {
    private final Path notesFile = Path.of("focusflow_notes.txt");

    public void saveNotes(String notes) {
        try {
            Files.writeString(notesFile, notes == null ? "" : notes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Unable to save notes: " + e.getMessage());
        }
    }

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
