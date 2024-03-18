package services;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedHashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        remove(id);
        history.put(id, task);
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history.values());
    }
}
