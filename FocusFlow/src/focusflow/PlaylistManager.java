package focusflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the FocusFlow music playlist by saving and loading selected audio files.
 *
 *  UMGC CMSC 495 Summer 2026
 *  Laris Ndamo – Project Manager/UI/UX Designer
 *  Robert Dayton – Frontend Developer
 *  Dion Hatchett – Backend/API Developer
 *  Tyson Howery – Frontend Developer
 *  Chris McCourt – Tester/Documentation Specialist
 *
 *  Version 1.0
 *
 * The playlist is stored in a local text file where each line represents the absolute
 * file path of a song. When loading the playlist, only files that still exist and are
 * valid files are added back into the playlist.
 */
public class PlaylistManager {
    private final Path playlistFile = Path.of("focusflow_playlist.txt");

    /**
     * Saves the current playlist to the playlist storage file.
     *
     * Each non-null song in the playlist is stored as an absolute file path.
     * Existing playlist data is overwritten each time this method is called.
     *
     * @param playlist the list of music files to save
     */
    public void savePlaylist(List<File> playlist) {
        List<String> filePaths = new ArrayList<>();

        for (File song : playlist) {
            if (song != null) {
                filePaths.add(song.getAbsolutePath());
            }
        }

        try {
            Files.write(
                    playlistFile,
                    filePaths,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Unable to save playlist: " + e.getMessage());
        }
    }

    /**
     * Loads the saved playlist from the playlist storage file.
     *
     * Each saved file path is converted back into a File object. Files that no longer
     * exist or are not valid files are skipped.
     *
     * @return a list of valid music files loaded from the saved playlist
     */
    public List<File> loadPlaylist() {
        List<File> playlist = new ArrayList<>();

        try {
            if (Files.exists(playlistFile)) {
                List<String> filePaths = Files.readAllLines(playlistFile);

                for (String filePath : filePaths) {
                    File song = new File(filePath);

                    if (song.exists() && song.isFile()) {
                        playlist.add(song);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to load playlist: " + e.getMessage());
        }

        return playlist;
    }
}
