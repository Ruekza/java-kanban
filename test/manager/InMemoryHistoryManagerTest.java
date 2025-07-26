package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Task task1 = new Task(1, "Отпуск", "Купить билеты", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task1);
        List<Task> history1 = historyManager.getHistory();
        Assertions.assertEquals(1, history1.size(), "После добавления одной задачи размер должен быть 1");
        Task task2 = new Task(1, "Отпуск", "Купить билеты", Status.DONE, LocalDateTime.of(2025, 10, 25, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task2);
        List<Task> history2 = historyManager.getHistory();
        Assertions.assertEquals(1, history2.size(), "После добавления обновленной задачи размер должен быть 1");
    }

    @Test
    public void checkTaskDoNotSavedPreviousVersionWhenAddedInHistoryManager2() { // 2 версия теста
        Task task1 = new Task("Отпуск", "Купить билеты", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask = taskManager.createTask(task1);
        taskManager.getTask(savedTask.getId());
        Task task2 = new Task(savedTask.getId(), "Отпуск", "Купить билеты", Status.DONE, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        taskManager.updateTask(task2);
        taskManager.getTask(savedTask.getId());
        List<Task> tasks = taskManager.getHistory();
        Assertions.assertEquals(1, tasks.size(), "После добавления обновленной задачи размер должен быть 2");
    }

    @Test
    public void canAddTaskInHistory1() {  // 1 версия
        Task task1 = new Task(1, "task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size(), "После добавления одной задачи размер должен быть 1");
    }

    @Test
    public void canAddTaskInHistory2() { // 2 версия
        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask1 = taskManager.createTask(task1);
        taskManager.getTask(savedTask1.getId());
        List<Task> tasks = taskManager.getHistory();
        Assertions.assertEquals(1, tasks.size(), "История просмотров должна содержать 1 просмотр");
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask2 = taskManager.createTask(task2);
        taskManager.getTask(savedTask2.getId());
        List<Task> tasks2 = taskManager.getHistory();
        Assertions.assertEquals(2, tasks2.size(), "История просмотров должна содержать 2 просмотра");
    }

    @Test
    public void canRemoveTaskInHistory() {
        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask1 = taskManager.createTask(task1);
        taskManager.getTask(savedTask1.getId());
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask2 = taskManager.createTask(task2);
        taskManager.getTask(savedTask2.getId());
        List<Task> tasks1 = taskManager.getHistory();
        Assertions.assertEquals(2, tasks1.size());
        taskManager.deleteTask(savedTask1.getId());
        List<Task> tasks2 = taskManager.getHistory();
        Assertions.assertEquals(1, tasks2.size());
    }

    @Test
    public void canRemoveTaskFromHistoryById() {
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
        Task task1 = new Task(1, "task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task1);
        Task task2 = new Task(2, "task2", "desc2", Status.IN_PROGRESS, LocalDateTime.of(2025, 10, 25, 14, 02), Duration.ofMinutes(45));
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "После добавления двух задач размер должен быть 2");
        historyManager.remove(1);
        List<Task> history1 = historyManager.getHistory();
        Assertions.assertEquals(1, history1.size(), "После удаления одной задачи размер должен быть 1");
    }

    @Test
    public void checkRemoveSomeTasksFromHistory() {
        Epic epic = new Epic(1, "epic", "desc_epic", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), new ArrayList<>(List.of(2, 3)));
        Subtask sub1 = new Subtask(2, "subtask1", "desc_sub1", Status.NEW, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), 1);
        Subtask sub2 = new Subtask(3, "subtask2", "desc_sub2", Status.NEW, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), 1);
        historyManager.add(epic);
        historyManager.add(sub1);
        historyManager.add(sub2);
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(3, history.size());
        historyManager.removeSomeTasksFromHistory(List.of(2, 3));
        List<Task> history1 = historyManager.getHistory();
        Assertions.assertEquals(1, history1.size(), "После удаления двух подзадач размер должен быть 1");
    }
}