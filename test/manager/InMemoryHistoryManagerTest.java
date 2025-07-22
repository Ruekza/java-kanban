package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.util.List;


public class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryHistoryManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }

    @Test
    public void checkTaskDoNotSavedPreviousVersionWhenAddedInHistoryManager1() { // 1 версия теста
        Task task1 = new Task(1, "Отпуск", "Купить билеты", Status.NEW);
        historyManager.add(task1);
        List<Task> history1 = historyManager.getHistory();
        Assertions.assertEquals(1, history1.size(), "После добавления одной задачи размер должен быть 1");
        Task task2 = new Task(1, "Отпуск", "Купить билеты", Status.DONE);
        historyManager.add(task2);
        List<Task> history2 = historyManager.getHistory();
        Assertions.assertEquals(1, history2.size(), "После добавления обновленной задачи размер должен быть 1");
    }

    @Test
    public void checkTaskDoNotSavedPreviousVersionWhenAddedInHistoryManager2() { // 2 версия теста
        Task task1 = new Task("Отпуск", "Купить билеты", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        taskManager.getTask(savedTask.getId());
        Task task2 = new Task(savedTask.getId(), "Отпуск", "Купить билеты", Status.DONE);
        taskManager.updateTask(task2);
        taskManager.getTask(savedTask.getId());
        List<Task> tasks = taskManager.getHistory();
        Assertions.assertEquals(1, tasks.size(), "После добавления обновленной задачи размер должен быть 2");
    }

    @Test
    public void canAddTaskInHistory1() {  // 1 версия
        Task task1 = new Task(1, "task1", "desc1", Status.NEW);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "После добавления одной задачи размер должен быть 1");
    }

    @Test
    public void canAddTaskInHistory2() { // 2 версия
        Task task1 = new Task("task1", "desc1", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        taskManager.getTask(savedTask1.getId());
        List<Task> tasks = taskManager.getHistory();
        Assertions.assertEquals(1, tasks.size(), "История просмотров должна содержать 1 просмотр");
        Task task2 = new Task("task2", "desc2", Status.NEW);
        Task savedTask2 = taskManager.createTask(task2);
        taskManager.getTask(savedTask2.getId());
        List<Task> tasks2 = taskManager.getHistory();
        Assertions.assertEquals(2, tasks2.size(), "История просмотров должна содержать 2 просмотра");
    }

    @Test
    public void canRemoveTaskInHistory() {
        Task task1 = new Task("task1", "desc1", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        taskManager.getTask(savedTask1.getId());
        Task task2 = new Task("task2", "desc2", Status.NEW);
        Task savedTask2 = taskManager.createTask(task2);
        taskManager.getTask(savedTask2.getId());
        List<Task> tasks1 = taskManager.getHistory();
        Assertions.assertEquals(2, tasks1.size());
        taskManager.deleteTask(savedTask1.getId());
        List<Task> tasks2 = taskManager.getHistory();
        Assertions.assertEquals(1, tasks2.size());
    }


}