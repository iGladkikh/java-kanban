package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static int totalTasksCount = 0;
    private final int id;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description) {
        this.id = getNextId();
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, Status status) {
        this.id = getNextId();
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;

        if (id > totalTasksCount) {
            setNextId(id);
        }
    }

    public Task(
            int id,
            String name,
            String description,
            Status status,
            LocalDateTime starTime,
            Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = starTime;
        this.duration = duration;

        if (id > totalTasksCount) {
            setNextId(id);
        }
    }

    public Task(
            String name,
            String description,
            String status,
            String starTime,
            int duration) {
        this.id = getNextId();
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(status);
        this.startTime = LocalDateTime.parse(starTime, DATE_TIME_FORMATTER);
        this.duration = Duration.ofMinutes(duration);
    }

    private static int getNextId() {
        return ++totalTasksCount;
    }

    private static void setNextId(int value) {
        totalTasksCount = value;
    }

    public static Task createFromString(String value, String fieldDelimiter) {
        String[] data = value.split(fieldDelimiter, -1);
        LocalDateTime starTime = null;
        Duration duration = null;
        if (data.length > 4) {
            if (!data[4].isBlank()) {
                starTime = LocalDateTime.parse(data[4], DATE_TIME_FORMATTER);
            }

            if (!data[5].isBlank()) {
                duration = Duration.ofMinutes(Integer.parseInt(data[5]));
            }
        }

        return new Task(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                Status.valueOf(data[3]),
                starTime,
                duration
        );
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isPrioritized() {
        return getStartTime() != null && getEndTime() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", starTime='" + (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "") + '\'' +
                ", duration=" + (duration != null ? duration.toMinutes() : 0) +
                '}';
    }

    public String toSaveString(String delimiter) {
        String starTimeString = startTime != null ? startTime.format(DATE_TIME_FORMATTER) : "";
        String durationString = duration != null ? String.valueOf(duration.toMinutes()) : "";
        String[] s = new String[]{Type.TASK.toString(), String.valueOf(id), name, description, String.valueOf(status), starTimeString, durationString};
        return String.join(delimiter, s);
    }

    public LocalDateTime getEndTime() {
        try {
            return startTime.plus(duration);
        } catch (NullPointerException e) {
            return null;
        }
    }
}