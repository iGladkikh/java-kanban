package services;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node last;
    private final Map<Integer, Node> history;

    public InMemoryHistoryManager() {
        this.last = null;
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        remove(id);

        Node node = new Node(task, last);
        linkLast(node);
        last = node;
        history.put(id, last);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            removeNode(history.get(id));
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        if (history.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Task> result = new ArrayList<>(history.size());
        Node node = last;
        result.add(node.data);
        while (node.hasPrev()) {
            result.add(node.prev.data);
            node = node.prev;
        }
        return result;
    }

    public void linkLast(Node newNode) {
        if (last != null) {
            last.next = newNode;
        }
        last = newNode;
    }

    public void removeNode(Node node) {
        if (node.equals(last)) {
            last = node.prev;
            if (last != null) {
                last.next = null;
            }
        } else {
            if (node.hasPrev()) {
                node.prev.next = node.next;
            }
            if (node.hasNext()) {
                node.next.prev = node.prev;
            }
        }
    }

    private static class Node {
        private final Task data;
        private Node prev;
        private Node next;

        Node(Task task, Node prev) {
            this.prev = prev;
            this.data = task;
            this.next = null;
        }

        public boolean hasPrev() {
            return prev != null;
        }

        public boolean hasNext() {
            return next != null;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return data.getId() == node.data.getId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(data.getId());
        }
    }
}
