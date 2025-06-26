package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> nodes = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    @Override
    public List<Task> getHistory() {
        List<Task> historyTask = new ArrayList<>();
        Node<Task> node = first;
        while (node != null) {
            historyTask.add(node.getValue());
            node = node.getNext();
        }
        return new ArrayList<>(historyTask);
    }


    @Override
    public void remove(int id) { // удалить задачу из истории просмотров
        if (nodes.containsKey(id)) {
            Node<Task> node = nodes.get(id);
            removeNode(node);
            nodes.remove(id);
        }
    }

    @Override
    public void removeAllHistory(List<Integer> idList) {
        for (Integer id : idList) {
            remove(id);
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        nodes.put(task.getId(), last);
    }

    private void removeNode(Node<Task> node) {
        if (first == node) {
            first = first.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
        } else if (last == node) {
            last = last.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }


    private void linkLast(Task task) {
        Node<Task> node = new Node<>(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
    }

    private static class Node<T> {

        private T value;
        private Node<T> prev;
        private Node<T> next;

        public Node(T value, Node<T> prev, Node<T> next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public T getValue() {
            return value;
        }

        public Node<T> getNext() {
            return next;
        }
    }
}

