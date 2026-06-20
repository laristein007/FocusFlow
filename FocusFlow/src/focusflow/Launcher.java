package focusflow;

/**
 * Launcher class for the FocusFlow application.
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
 * This class provides a simple entry point for starting the FocusFlow JavaFX
 * application. It delegates execution to the main method in FocusFlowApp.
 *
 * Main responsibilities:
 * - Provide the application's main launch entry point
 * - Start the FocusFlow JavaFX application
 * - Forward command-line arguments to FocusFlowApp
 */
public class Launcher {
    /**
     * Starts the FocusFlow application.
     *
     * This method passes any command-line arguments directly to FocusFlowApp,
     * which launches the JavaFX user interface.
     *
     * @param args command-line arguments passed when the application starts
     */
    public static void main(String[] args) {
        FocusFlowApp.main(args);
    }
}
