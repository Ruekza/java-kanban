package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
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
import java.util.Objects;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected TaskManagerTest() throws IOException {
    }

    protected abstract TaskManager createTaskManager() throws IOException;

    TaskManager taskManager = createTaskManager(); // Метод createTaskManager должен быть реализован в подклассе


    public void checkTasksEqualsById() {
        Task task = new Task("Отпуск", "Купить билеты", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        Task savedTask = taskManager.createTask(task); // создается задача с определенным id
        Assertions.assertEquals(task, taskManager.getTask(savedTask.getId()), "Задачи не равны");
    }

    @Test
    public void checkEpicsEqualsById() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, LocalDateTime.of(2025, 6, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Assertions.assertEquals(epic, taskManager.getEpic(savedEpic.getId()), "Эпики не равны");
    }

    @Test
    public void checkSubtasksEqualsById() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Помыть полы", "Без химии", Status.NEW, LocalDateTime.of(2025, 5, 26, 14, 02), Duration.ofMinutes(45), savedEpic.getId());
        Task savedSubtask = taskManager.createSubtask(subtask);
        Assertions.assertEquals(subtask, taskManager.getSubtask(savedSubtask.getId()), "Подзадачи не равны");
    }

    @Test
    public void canNotAddEpicIntoEpic() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        taskManager.createEpic(epic);
        Assertions.assertFalse(epic.getSubtaskId().contains(epic.getId()), "Эпик не может быть добавлен в качестве подзадачи в самого себя");
    }

    @Test
    public void canNotAddSubtaskIntoSubtask() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Помыть полы", "Без химии", Status.NEW, LocalDateTime.of(2025, 3, 26, 14, 02), Duration.ofMinutes(45), epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertFalse(subtask.getId().equals(subtask.getEpicId()), "Подзадача не может быть добавлена в качестве своего эпика");
    }

    @Test
    public void canCreateTaskAndFindById() {
        Task task = new Task("name", "desc", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45));
        taskManager.createTask(task);
        ArrayList<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(1, tasks.size());
        int id = task.getId();
        Task savedTask = taskManager.getTask(id);
        Assertions.assertEquals(task, savedTask);
    }


    @Test
    public void canCreateSubtaskAndFindById() {
        Epic epic = new Epic("Приготовить ужин", "Салат+чай", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сделать салат", "Греческй салат", Status.NEW, LocalDateTime.of(2025, 8, 01, 14, 15), Duration.ofMinutes(45), savedEpic.getId());
        taskManager.createSubtask(subtask);
        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(1, subtasks.size());
        int id = subtask.getId();
        Subtask savedSubtask = taskManager.getSubtask(id);
        Assertions.assertEquals(subtask, savedSubtask);
    }

    @Test
    public void canCreateEpicAndFindById() {
        Epic epic = new Epic("Приготовить ужин", "Салат+чай", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        taskManager.createEpic(epic);
        ArrayList<Epic> epics = taskManager.getEpics();
        Assertions.assertEquals(1, epics.size());
        int id = epic.getId();
        Epic savedEpic = taskManager.getEpic(id);
        Assertions.assertEquals(epic, savedEpic);
    }

    @Test
    public void canCreateTasksWithGivenIdAndGeneratedId() {
        Task task1 = new Task(1, "name1", "desc1", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(60));
        taskManager.createTask(task1);
        Task task2 = new Task("name2", "desc2", Status.NEW, LocalDateTime.of(2025, 2, 26, 14, 02), Duration.ofMinutes(45));
        taskManager.createTask(task2);
        ArrayList<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertTrue(!Objects.equals(task1.getId(), task2.getId()));
    }

    @Test
    public void doesNotChangeTaskWhenAddingtoTaskManager() {
        Task task = new Task("name", "desc", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45));
        taskManager.createTask(task);
        Task savedTask = taskManager.getTask(task.getId());
        Assertions.assertTrue(task.getName().equals(savedTask.getName()) && task.getDescription().equals(savedTask.getDescription()) && task.getStatus() == savedTask.getStatus());
    }

    @Test
    public void checkEpicDoesNotHaveRemovedSubtusk() {
        Epic epic = new Epic("epic", "desc_epic", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("subtask1", "desc_sub1", Status.NEW, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId());
        Subtask sub2 = new Subtask("subtask2", "desc_sub2", Status.NEW, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), savedEpic.getId());
        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);
        List<Integer> list1 = epic.getSubtaskId();
        Assertions.assertEquals(2, list1.size());
        taskManager.deleteSubtask(sub2.getId());
        List<Integer> list2 = epic.getSubtaskId();
        Assertions.assertEquals(1, list2.size());
    }

    @Test
    public void checkStatusOfEpic() {
        Epic epic = new Epic("epic", "desc_epic", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("subtask1", "desc_sub1", Status.NEW, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId());
        Subtask sub2 = new Subtask("subtask2", "desc_sub2", Status.NEW, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), savedEpic.getId());
        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);
        Assertions.assertEquals(Status.NEW, savedEpic.getStatus());
        taskManager.updateSubtask(new Subtask(sub1.getId(), "subtask1", "desc_sub1", Status.DONE, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId()));
        taskManager.updateSubtask(new Subtask(sub2.getId(), "subtask2", "desc_sub2", Status.DONE, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), savedEpic.getId()));
        Assertions.assertEquals(Status.DONE, savedEpic.getStatus());
        taskManager.updateSubtask(new Subtask(sub1.getId(), "subtask1", "desc_sub1", Status.NEW, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId()));
        taskManager.updateSubtask(new Subtask(sub2.getId(), "subtask2", "desc_sub2", Status.DONE, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), savedEpic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, savedEpic.getStatus());
        taskManager.updateSubtask(new Subtask(sub1.getId(), "subtask1", "desc_sub1", Status.IN_PROGRESS, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId()));
        taskManager.updateSubtask(new Subtask(sub2.getId(), "subtask2", "desc_sub2", Status.IN_PROGRESS, LocalDateTime.of(2025, 4, 21, 14, 00), Duration.ofMinutes(90), savedEpic.getId()));
        Assertions.assertEquals(Status.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    public void checkSubtaskHasEpic() {
        Epic epic = new Epic("epic", "desc_epic", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(45), null);
        Epic savedEpic = taskManager.createEpic(epic);
        Assertions.assertTrue(savedEpic.getSubtaskId().isEmpty());
        Subtask sub = new Subtask("sub", "desc_sub", Status.NEW, LocalDateTime.of(2025, 4, 20, 14, 00), Duration.ofMinutes(45), savedEpic.getId());
        Subtask savedSub = taskManager.createSubtask(sub);
        Assertions.assertFalse(savedEpic.getSubtaskId().isEmpty());
        Assertions.assertEquals(savedEpic.getSubtaskId().get(0), savedSub.getId());
        Assertions.assertEquals(sub.getEpicId(), savedEpic.getId());
    }

    @Test
    public void checkIsCrossing() {
        Task task1 = new Task(1, "name1", "desc1", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(60));
        Task task2 = new Task(2, "name2", "desc2", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 30), Duration.ofMinutes(45));
        Assertions.assertTrue(taskManager.isCrossing(task1, task2));
        Task task3 = new Task(3, "name3", "desc3", Status.NEW, LocalDateTime.of(2025, 4, 26, 22, 30), Duration.ofMinutes(45));
        Assertions.assertFalse(taskManager.isCrossing(task1, task3));
    }

    @Test
    public void checkCrossingTasksException() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            Task task1 = new Task("name1", "desc1", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 02), Duration.ofMinutes(60));
            taskManager.createTask(task1);
            Task task2 = new Task("name2", "desc2", Status.NEW, LocalDateTime.of(2025, 4, 26, 14, 30), Duration.ofMinutes(45));
            taskManager.createTask(task2);
        });
    }

}