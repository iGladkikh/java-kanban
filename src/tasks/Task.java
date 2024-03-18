package tasks;

import java.util.Objects;

public class Task {
    private static int totalTasksCount = 0;
    private final int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        id = getNextId();
        status = Status.NEW;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, Status status) {
        id = getNextId();
        this.name = name;
        this.description = description;
        this.status = status;
    }

    Task(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
    }

    private static int getNextId() {
        return ++totalTasksCount;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        return "tasks.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public Task copy() {
        return new Task(this);
    }
}