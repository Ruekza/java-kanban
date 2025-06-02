package manager;

import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;


import java.util.List;

public class ManagersTest {


    @Test
    public void CreateAndWorkTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager); //проверяем, что объект-менеджер есть и не нулевой
        Task task = new Task("name", "desc", Status.NEW);
        Assertions.assertEquals(task, taskManager.createTask(task)); //проверяем, что объект-менеджер работает(выполняет методы)
    }

    @Test
    public void CreateAndWorkHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager); //проверяем, что объект-менеджер истории есть и не нулевой
        Task task = new Task("name", "desc", Status.NEW);
        historyManager.add(task); //проверяем, что объект-менеджер истории работает(выполняет свои методы)
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());


    }
}
