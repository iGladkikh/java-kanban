package tasks;

import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashSet<>();
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtaskCount=" + subtasks.size() +
                '}';
    }
}