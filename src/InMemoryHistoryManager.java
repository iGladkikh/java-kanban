import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_MAX_ITEMS_COUNT = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(HISTORY_MAX_ITEMS_COUNT);
    }

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_MAX_ITEMS_COUNT) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
