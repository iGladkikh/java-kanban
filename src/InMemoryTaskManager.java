import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistoryManager();
    }

    private boolean isTaskContains(Task task) {
        int id = task.getId();
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    private void calculateEpicStatus(Epic epic) {
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
        if (size == 1 && statuses.contains(Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (size == 1 && statuses.contains(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public int addEpic(Epic epic) {
        if (isTaskContains(epic)) return -1;

        int epicId = epic.getId();
        epics.put(epicId, epic);
        return epicId;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic tergetEpic = epics.get(id);
            tergetEpic.setName(epic.getName());
            tergetEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void removeEpic(int id) {
        clearEpicSubtasks(id);
        epics.remove(id);
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Map<Integer, Subtask> getEpicSubtasks(int epicId) {
        Map<Integer, Subtask> result = new HashMap<>();
        if (epics.containsKey(epicId)) {
            for (int subtaskId : epics.get(epicId).getSubtasks()) {
                result.put(subtaskId, subtasks.get(subtaskId));
            }
        }
        return result;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId) && !isTaskContains(subtask)) {
            Epic epic = epics.get(epicId);
            int subtaskId = subtask.getId();
            epic.addSubtask(subtaskId);
            subtasks.put(subtaskId, subtask);
            calculateEpicStatus(epic);
            return subtaskId;
        }
        return -1;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);
            calculateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        Epic epic = epics.get(epicId);
        epic.removeSubtask(id);
        calculateEpicStatus(epic);
        subtasks.remove(id);
    }

    @Override
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

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
        subtasks.clear();
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public int addTask(Task task) {
        if (isTaskContains(task)) return -1;

        int taskId = task.getId();
        tasks.putIfAbsent(taskId, task);
        return taskId;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllData() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}