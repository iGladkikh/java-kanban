import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_ITEMS_COUNT = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(HISTORY_ITEMS_COUNT);
    }

    @Override
    public void add(Task task) {
        history.addLast(task);
        if (history.size() > HISTORY_ITEMS_COUNT) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
