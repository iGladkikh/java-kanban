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
        super(id, name, description);
        this.subtasks = new HashSet<>();
    }

    public static Epic createFromString(String value, String fieldDelimiter) {
        Task task = Task.createFromString(value, fieldDelimiter);
        return new Epic(
                task.getId(),
                task.getName(),
                task.getDescription()
        );
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
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", starTime='" + (getStartTime() != null ? getStartTime().format(DATE_TIME_FORMATTER) : "") + '\'' +
                ", endTime='" + (getEndTime() != null ? getEndTime().format(DATE_TIME_FORMATTER) : "") + '\'' +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : 0) +
                ", subtaskCount=" + subtasks.size() +
                '}';
    }

    @Override
    public String toSaveString(String delimiter) {
        String[] s = new String[]{Type.EPIC.toString(), String.valueOf(getId()), getName(), getDescription()};
        return String.join(delimiter, s);
    }
}