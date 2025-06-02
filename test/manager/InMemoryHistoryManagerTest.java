package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;


public class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    public void checkTaskSavedPreviousVersionWhenAddedInHistoryManager1() { // 1 версия теста
        Task task1 = new Task(1, "Отпуск", "Купить билеты", Status.NEW);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "После добавления одной задачи размер должен быть 1");
        Task task2 = new Task(1, "Отпуск", "Купить билеты", Status.DONE);
        historyManager.add(task2);
        List<Task> historyNew = historyManager.getHistory();
        Assertions.assertEquals(2, historyNew.size(), "После добавления обновленной задачи размер должен быть 2");
    }

    @Test
    public void checkTaskSavedPreviousVersionWhenAddedInHistoryManager2() { // 2 версия теста
        Task task1 = new Task("Отпуск", "Купить билеты", Status.NEW);
        taskManager.createTask(task1);
        taskManager.getTask(1);
        Task task2 = new Task(1, "Отпуск", "Купить билеты", Status.DONE);
        taskManager.updateTask(task2);
        taskManager.getTask(1);
        List<Task> tasks = taskManager.getHistory();
        Assertions.assertEquals(2, tasks.size(), "После добавления обновленной задачи размер должен быть 2");
    }

}