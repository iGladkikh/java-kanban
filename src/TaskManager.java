import java.util.List;
import java.util.Map;

public interface TaskManager {

    Map<Integer, Epic> getEpics();

    int addEpic(Epic epic);

    Epic getEpic(int id);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    void clearEpics();

    Map<Integer, Subtask> getEpicSubtasks(int epicId);

    Map<Integer, Subtask> getSubtasks();

    int addSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void updateSubtask(Subtask subtask);

    void removeSubtask(int id);

    void clearEpicSubtasks(int id);

    void clearSubtasks();

    Map<Integer, Task> getTasks();

    int addTask(Task task);

    Task getTask(int id);

    void updateTask(Task task);

    void removeTask(int id);

    void clearTasks();

    void clearAllData();

    List<Task> getHistory();
}
