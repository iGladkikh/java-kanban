package services;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks;
    protected final HistoryManager historyManager;

    InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.historyManager = historyManager;
    }

    private boolean isTaskContains(Task task) {
        int id = task.getId();
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    private void computeEpicFields(Epic epic) {
        Set<Integer> epicSubtasks = epic.getSubtasks();
        Set<Status> subtasksStatuses = new HashSet<>();
        Set<LocalDateTime> subtasksDates = new TreeSet<>();
        for (int subtaskId : epicSubtasks) {
            Subtask subtask = subtasks.get(subtaskId);
            subtasksStatuses.add(subtask.getStatus());
            LocalDateTime startTime = subtask.getStartTime();
            if (startTime != null) {
                subtasksDates.add(startTime);
            }
            LocalDateTime endTime = subtask.getEndTime();
            if (startTime != null) {
                subtasksDates.add(endTime);
            }
        }

        setEpicStatus(epic, subtasksStatuses);
        setEpicPriority(epic, subtasksDates);
    }

    private void setEpicStatus(Epic epic, Set<Status> subtasksStatuses) {
        int size = subtasksStatuses.size();
        if (size == 0 || size == 1 && subtasksStatuses.contains(Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (size == 1 && subtasksStatuses.contains(Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void setEpicPriority(Epic epic, Set<LocalDateTime> sortedDates) {
        if (sortedDates.isEmpty()) return;
        List<LocalDateTime> dates = List.copyOf(sortedDates);
        epic.setStartTime(dates.getFirst());
        epic.setEndTime(dates.getLast());
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return Map.copyOf(epics);
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
        reloadPrioritizedTasks();
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
        reloadPrioritizedTasks();
    }

    @Override
    public Map<Integer, Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) return new HashMap<>();
        return epics.get(epicId).getSubtasks().stream()
                .collect(Collectors.toMap(id -> id, subtasks::get, (a, b) -> b));
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return Map.copyOf(subtasks);
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId) && !isTaskContains(subtask) &&
                (!subtask.isPrioritized() || !isIntersectedTask(subtask))) {
            Epic epic = epics.get(epicId);
            int subtaskId = subtask.getId();
            epic.addSubtask(subtaskId);
            subtasks.put(subtaskId, subtask);
            computeEpicFields(epic);
            if (subtask.isPrioritized()) {
                prioritizedTasks.add(subtask);
            }
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
            computeEpicFields(epics.get(subtask.getEpicId()));
            prioritizedTasks.remove(subtask);
            if (subtask.isPrioritized() && !isIntersectedTask(subtask)) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            Epic epic = epics.get(epicId);
            epic.removeSubtask(id);
            computeEpicFields(epic);
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void clearEpicSubtasks(int id) {
        if (!epics.containsKey(id)) return;
        Epic epic = epics.get(id);
        epic.getSubtasks().stream()
                .forEach(subtaskId -> {
                    prioritizedTasks.remove(subtasks.get(subtaskId));
                    subtasks.remove(subtaskId);
                    historyManager.remove(subtaskId);
                });
        epic.clearSubtasks();
        epic.setStatus(Status.NEW);
    }

    @Override
    public void clearSubtasks() {
        epics.keySet().stream()
                .forEach(this::clearEpicSubtasks);
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return Map.copyOf(tasks);
    }

    @Override
    public int addTask(Task task) {
        if (isTaskContains(task) || task.getClass() != Task.class) return -1;

        int taskId = task.getId();
        if (!task.isPrioritized() || !isIntersectedTask(task)) {
            tasks.putIfAbsent(taskId, task);
            if (task.isPrioritized()) {
                prioritizedTasks.add(task);
            }
        }
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
            prioritizedTasks.remove(task);
            if (task.isPrioritized() && !isIntersectedTask(task)) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void removeTask(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void clearTasks() {
        tasks.clear();
        reloadPrioritizedTasks();
    }

    @Override
    public void clearAllData() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void reloadPrioritizedTasks() {
        prioritizedTasks.clear();
        Stream.concat(getTasks().values().stream(), getSubtasks().values().stream())
                .filter(Task::isPrioritized)
                .sorted(Comparator.comparing(Task::getStartTime))
                .forEach(prioritizedTasks::add);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isIntersectedTask(Task task) {
        if (!task.isPrioritized()) return false;
        return prioritizedTasks.stream()
                .anyMatch(t -> t.getStartTime().equals(task.getStartTime()) ||
                        (t.getStartTime().isBefore(task.getEndTime()) &&
                                t.getEndTime().isAfter(task.getStartTime())));
    }
}