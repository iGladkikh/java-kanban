import java.util.HashMap;
import java.util.HashSet;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> tasks;

    public Epic(String name, String description) {
        super(name, description);
        this.tasks = new HashMap<>();
    }

    public Epic(String name, String description, HashMap<Integer, Subtask> tasks) {
        super(name, description);
        this.tasks = tasks;
        updateStatus();
    }

    public void updateStatus() {
        if (tasks.isEmpty()) {
            this.status = Status.NEW;
            return;
        }

        HashSet<Status> statuses = new HashSet<>();
        for (Subtask task : tasks.values()) {
            if (task.getStatus() == Status.NEW) {
                statuses.add(Status.NEW);
                break;
            } else if (task.getStatus() == Status.IN_PROGRESS) {
                statuses.add(Status.IN_PROGRESS);
            }
        }

        if (statuses.contains(Status.NEW)) {
            this.status = Status.NEW;
        } else if (statuses.contains(Status.IN_PROGRESS)) {
            this.status = Status.IN_PROGRESS;
        } else {
            this.status = Status.DONE;
        }
    }

    public void addTask(Subtask task) {
        tasks.put(task.getId(), task);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public HashMap<Integer, Subtask> getTasks() {
        return tasks;
    }
}
