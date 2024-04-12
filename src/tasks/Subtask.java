package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(
            int id,
            String name,
            String description,
            Status status,
            LocalDateTime startTime,
            Duration duration,
            int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public static Subtask createFromString(String value, String fieldDelimiter) {
        String[] data = value.split(fieldDelimiter, -1);
        if (data.length < 7) {
            throw new IllegalArgumentException("Subtask requires at least 7 parameters");
        }

        Task task = Task.createFromString(value, fieldDelimiter);
        return new Subtask(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getStartTime(),
                task.getDuration(),
                Integer.parseInt(data[6])
        );
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", starTime='" + (getStartTime() != null ? getStartTime().format(DATE_TIME_FORMATTER) : "") + '\'' +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() : 0) +
                '}';
    }

    @Override
    public String toSaveString(String delimiter) {
        return super.toSaveString(delimiter) + "," + getEpicId();
//        return String.join(delimiter, new String[]{
//                Type.SUBTASK.toString(), String.valueOf(getId()), getName(), getDescription(),
//                String.valueOf(getStatus()), String.valueOf(getEpicId())
//        });
    }
}