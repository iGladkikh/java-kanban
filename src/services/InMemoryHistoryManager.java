package services;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node first;
    private Node last;
    private final Map<Integer, Node> history;

    public InMemoryHistoryManager() {
        this.first = null;
        this.last = null;
        this.history = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();
        remove(id);

        Node node = new Node(task, last);
        if (first == null) {
            first = node;
        }
        if (last != null) {
            linkLast(node);
        }
        last = node;
        history.put(id, node);
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
        ArrayList<Task> result = new ArrayList<>(history.size());
        Node tmp = first;
        while (tmp != null) {
            result.add(tmp.data);
            tmp = tmp.next;
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
        if (node.equals(first) && node.equals(last)) {
            first = null;
            last = null;
        } else if (node.equals(first)) {
            first = node.next;
            if (first != null) {
                first.prev = null;
            }
        } else if (node.equals(last)) {
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
