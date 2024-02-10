import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private boolean isTaskContains(Task task) {
        int id = task.getId();
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public int addEpic(Epic epic) {
        if (isTaskContains(epic)) return -1;

        int epicId = epic.getId();
        epics.put(epicId, epic);
        return epicId;
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic tergetEpic = epics.get(id);
            tergetEpic.setName(epic.getName());
            tergetEpic.setDescription(epic.getDescription());
        }
    }

    public void removeEpic(int id) {
        clearEpicSubtasks(id);
        epics.remove(id);
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    private void calculateEpicStatus(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        Epic epic = epics.get(epicId);
        Set<Integer> epicSubtasks = epic.getSubtasks();

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Set<Status> statuses = new HashSet<>();
        for (int subtaskId : epicSubtasks) {
            Subtask subtask = subtasks.get(subtaskId);
            statuses.add(subtask.getStatus());
        }

        int size = statuses.size();
        if (size == 1 && statuses.contains(Status.NEW)) { // statuses -> HashSet, при 2 задачах в статусе NEW сработает первое условие
            epic.setStatus(Status.NEW);
        } else if (size == 1 && statuses.contains(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public Map<Integer, Subtask> getEpicSubtasks(int epicId) {
        Map<Integer, Subtask> result = new HashMap<>();
        if (epics.containsKey(epicId)) {
            for (int subtaskId : epics.get(epicId).getSubtasks()) {
                result.put(subtaskId, subtasks.get(subtaskId));
            }
        }
        return result;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId) && !isTaskContains(subtask)) {
            int subtaskId = subtask.getId();
            epics.get(epicId).addSubtask(subtaskId);
            subtasks.put(subtaskId, subtask);
            calculateEpicStatus(epicId); // Метод вызывается также в строке 120, где объекта epic нет, поэтому передается id.
            return subtaskId;
        }
        return -1;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            calculateEpicStatus(subtask.getEpicId());
        }
    }

    public void removeSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).removeSubtask(id);
        calculateEpicStatus(epicId);
        subtasks.remove(id);
    }

    public void clearEpicSubtasks(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    public void clearAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public int addTask(Task task) {
        if (isTaskContains(task)) return -1;

        int taskId = task.getId();
        tasks.putIfAbsent(taskId, task);
        return taskId;
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
        subtasks.clear();
    }
}