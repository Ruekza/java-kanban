package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    public List<Task> getHistory();

    public void add(Task task);

    public void remove(int id);

    public void removeAllHistory();
}
