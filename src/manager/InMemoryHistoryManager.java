package manager;

import tasks.Task;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> historyTask = new ArrayList<>(10);

    @Override
    public ArrayList<Task> getHistory() {
        return historyTask;
    }

    @Override
    public void add(Task task) {
        if(historyTask.size()<10) {
            historyTask.add(task);
        } else {
            historyTask.remove(0);
            historyTask.add(task);
        }
    }
}
