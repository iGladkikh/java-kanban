import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;

    TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public void addEpic(Epic epic) {
        epics.putIfAbsent(epic.getId(), epic);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    public void clearEpics() {
        epics.clear();
    }

    public Map<Integer, Subtask> getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId).getSubtasks();
        } else {
            return new HashMap<>();
        }
    }

    public Map<Integer, Subtask> getAllSubtasks() {
        HashMap<Integer, Subtask> result = new HashMap<>();
        for (Epic epic : epics.values()) {
            result.putAll(epic.getSubtasks());
        }
        return result;
    }

    public void addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            epics.get(epicId).addSubtask(subtask);
        }
    }

    public Subtask getSubtaskById(int id) {
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().containsKey(id)) {
                return epic.getSubtasks().get(id);
            }
        }
        return null;
    }

    public void updateSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            epics.get(epicId).updateSubtask(subtask);
        }
    }

    public void removeSubtask(int id) {
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().containsKey(id)) {
                epic.removeSubtask(id);
            }
        }
    }

    public void clearEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).clearSubtasks();
        }
    }

    public void clearAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
        }
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.putIfAbsent(task.getId(), task);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearAllData() {
        tasks.clear();
        epics.clear();
    }
}