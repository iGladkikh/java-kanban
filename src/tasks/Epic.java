package tasks;

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashSet<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtasks = new HashSet<>();
    }

    private Epic(Epic task) {
        super(task);
        this.subtasks = task.getSubtasks();
    }

    public static Epic createFromString(String value, String fieldDelimiter) {
        String[] data = value.split(fieldDelimiter);
        return new Epic(Integer.parseInt(data[0]), data[1], data[2]);
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

    public Set<Integer> getSubtasks() {
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

    @Override
    public String toSaveString(String delimiter) {
        return String.join(delimiter, new String[]{
                Type.EPIC.toString(), String.valueOf(getId()), getName(), getDescription()
        });
    }

    @Override
    public Epic copy() {
        return new Epic(this);
    }
}