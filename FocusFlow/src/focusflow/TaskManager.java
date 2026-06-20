package focusflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the checklist tasks for the FocusFlow application.
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
 * This class stores tasks in memory while the application is running and also
 * saves them to a text file so they can be loaded again the next time the app
 * starts. Each task is saved as one line in the task file.
 *
 * Main responsibilities:
 * - Add new checklist tasks
 * - Delete checklist tasks
 * - Return a copy of the current task list
 * - Load saved tasks from the task file
 * - Save task changes to the task file
 */
public class TaskManager {
    private final List<String> tasks = new ArrayList<>();
    private final Path tasksFile = Path.of("focusflow_tasks.txt");

    /**
     * Adds a new task to the checklist.
     *
     * Blank or null tasks are ignored. When a valid task is added, the task list
     * is immediately saved to the task file.
     *
     * @param task the task text entered by the user
     */
    public void addTask(String task) {
        if (task != null && !task.isBlank()) {
            tasks.add(task.trim());
            saveTasks();
        }
    }

    /**
     * Deletes a task from the checklist.
     *
     * After the task is removed, the updated task list is saved to the task file.
     *
     * @param task the task text to remove
     */
    public void deleteTask(String task) {
        tasks.remove(task);
        saveTasks();
    }

    /**
     * Returns a copy of the current task list.
     *
     * A copy is returned instead of the original list so other classes cannot
     * directly change the internal task list.
     *
     * @return a copy of the saved checklist tasks
     */
    public List<String> getTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Loads checklist tasks from the task file.
     *
     * If the task file exists, each line is loaded as one checklist task. If the
     * file does not exist or cannot be read, the current task list remains empty.
     *
     * @return a copy of the loaded task list
     */
    public List<String> loadTasks() {
        try {
            if (Files.exists(tasksFile)) {
                tasks.clear();
                tasks.addAll(Files.readAllLines(tasksFile));
            }
        } catch (IOException e) {
            System.err.println("Unable to load tasks: " + e.getMessage());
        }

        return getTasks();
    }

    /**
     * Saves the current checklist tasks to the task file.
     *
     * The file is created if it does not already exist. Existing file contents
     * are replaced with the latest version of the task list.
     */
    private void saveTasks() {
        try {
            Files.write(
                    tasksFile,
                    tasks,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            System.err.println("Unable to save tasks: " + e.getMessage());
        }
    }
}
