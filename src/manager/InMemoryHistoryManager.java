package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyTask = new ArrayList<>();
    static final int MAX_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (historyTask.size() < MAX_SIZE) {
                historyTask.add(task);
            } else {
                historyTask.remove(0);
                historyTask.add(task);
            }
        }
    }
}

