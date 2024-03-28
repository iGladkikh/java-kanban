package tasks;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    private Subtask(Subtask task) {
        super(task);
        this.epicId = task.getEpicId();
    }

    public static Subtask createFromString(String value, String fieldDelimiter) {
        String[] data = value.split(fieldDelimiter);
        return new Subtask(Integer.parseInt(data[0]), data[1], data[2],
                Status.valueOf(data[3]), Integer.parseInt(data[4]));
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public Subtask copy() {
        return new Subtask(this);
    }

    @Override
    public String toSaveString(String delimiter) {
        return String.join(delimiter, new String[] {
                Type.SUBTASK.toString(), String.valueOf(getId()), getName(), getDescription(),
                String.valueOf(getStatus()), String.valueOf(getEpicId())
        });
    }
}