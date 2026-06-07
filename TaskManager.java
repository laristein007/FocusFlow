package focusflow;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<String> tasks = new ArrayList<>();

    public void addTask(String task) {
        if (task != null && !task.isBlank()) {
            tasks.add(task.trim());
        }
    }

    public void deleteTask(String task) {
        tasks.remove(task);
    }

    public List<String> getTasks() {
        return new ArrayList<>(tasks);
    }
}
