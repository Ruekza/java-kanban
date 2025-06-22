package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        List<Task> historyTask = new ArrayList<>();
        for(Node node : nodes.values()){
          historyTask.add(node.getValue());
        }
        return new ArrayList<>(historyTask);
    }

    @Override
    public void remove(int id) { // удалить задачу из истории просмотров
        if (nodes.containsKey(id)) {
            Node node = nodes.get(id);
            removeNode(node);
            nodes.remove(id);
        }
    }

    @Override
    public void add(Task task) {
        nodes.remove(task.getId());
        linkLast(task);
        nodes.put(task.getId(), last);
    }

    private void removeNode(Node node) {
        if(first == node) {
            first = first.next;
        } else if(last == node) {
            last = last.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private void linkLast(Task task) {
     Node node = new Node(task, last, null);
     if(first == null) {
         first = node;
     } else {
         last.next = node;
     }
     last = node;
    }

    private static class Node {

        private Task value;
        private Node prev;
        private Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public Task getValue() {
            return value;
        }
    }
}

