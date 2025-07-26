package manager;

import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ManagersTest {


    @Test
    public void CreateAndWorkTaskManager() throws IOException {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager); //проверяем, что объект-менеджер есть и не нулевой
        Task task = new Task("name", "desc", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45));
        Assertions.assertEquals(task, taskManager.createTask(task)); //проверяем, что объект-менеджер работает(выполняет методы)
    }

    @Test
    public void CreateAndWorkHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager); //проверяем, что объект-менеджер истории есть и не нулевой
        Task task = new Task(1, "name", "desc", Status.NEW, LocalDateTime.of(2025, 11, 26, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task); //проверяем, что объект-менеджер истории работает(выполняет свои методы)
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
    }
}
