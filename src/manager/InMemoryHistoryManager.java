package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> nodes = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;
    private List<Task> historyTask = new ArrayList<>();


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }

    @Override
    public void remove(int id) { // удалить задачу из истории просмотров
        if (nodes.containsKey(id)) {
            Node<Task> node = nodes.get(id);
            historyTask.remove(node.getValue()); // удаление задачи из списка историй
            removeNode(node);
            nodes.remove(id);
        }
    }

    @Override
    public void removeAllHistory() {
        historyTask.clear();
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
        nodes.put(task.getId(), last);
        historyTask.add(last.getValue()); // добавление задачи в список историй
    }

    private void removeNode(Node<Task> node) {
        if (first == node) {
            first = first.next;
        } else if (last == node) {
            last = last.prev;
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


    }
}

