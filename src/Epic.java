import java.util.HashMap;
import java.util.HashSet;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashMap<>();
    }

    public Epic(String name, String description, HashMap<Integer, Subtask> tasks) {
        super(name, description);
        this.subtasks = tasks;
        updateStatus();
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        HashSet<Status> statuses = new HashSet<>();
        for (Subtask task : subtasks.values()) {
            if (task.getStatus() == Status.NEW) {
                statuses.add(Status.NEW);
                break;
            } else if (task.getStatus() == Status.IN_PROGRESS) {
                statuses.add(Status.IN_PROGRESS);
            }
        }

        if (statuses.contains(Status.NEW)) {
            setStatus(Status.NEW);
        } else if (statuses.contains(Status.IN_PROGRESS)) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.DONE);
        }
    }

    public void addSubtask(Subtask subtask) {
        subtasks.putIfAbsent(subtask.getId(), subtask);
        updateStatus();
    }

    public void addSubtask(Task task) {
        Subtask subtask = new Subtask(task.getName(), task.getDescription(), task.getStatus(), getId());
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
        }
        updateStatus();
    }

    public void clearSubtasks() {
        subtasks.clear();
        setStatus(Status.NEW);
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
        updateStatus();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks.size() +
                '}';
    }
}