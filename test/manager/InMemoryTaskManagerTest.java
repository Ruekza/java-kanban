package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import java.util.ArrayList;

public class InMemoryTaskManagerTest {

    /* TaskManager taskManager = Managers.getDefault();*/
    TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }


    @Test
    public void checkTasksEqualsById() {
        Task task = new Task("Отпуск", "Купить билеты", Status.NEW);
        Task savedTask = taskManager.createTask(task); // создается задача с определенным id
        Assertions.assertEquals(task, taskManager.getTask(savedTask.getId()), "Задачи не равны");
    }

    @Test
    public void checkEpicsEqualsById() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, null);
        Epic savedEpic = taskManager.createEpic(epic);
        Assertions.assertEquals(epic, taskManager.getEpic(savedEpic.getId()), "Эпики не равны");
    }

    @Test
    public void checkSubtasksEqualsById() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Помыть полы", "Без химии", Status.NEW, savedEpic.getId());
        Task savedSubtask = taskManager.createSubtask(subtask);
        Assertions.assertEquals(subtask, taskManager.getSubtask(savedSubtask.getId()), "Подзадачи не равны");
    }

    @Test
    public void canNotAddEpicIntoEpic() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, null);
        taskManager.createEpic(epic);
        Assertions.assertFalse(epic.getSubtaskId().contains(epic.getId()), "Эпик не может быть добавлен в качестве подзадачи в самого себя");
    }

    @Test
    public void canNotAddSubtaskIntoSubtask() {
        Epic epic = new Epic("Уборка", "Почистить квартиру", Status.NEW, null);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Помыть полы", "Без химии", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertFalse(subtask.getId() == subtask.getEpicId(), "Подзадача не может быть добавлена в качестве своего эпика");
    }

    @Test
    public void canCreateTaskAndFindById() {
        Task task = new Task("name", "desc", Status.NEW);
        taskManager.createTask(task);
        ArrayList<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(1, tasks.size());
        int id = task.getId();
        Task savedTask = taskManager.getTask(id);
        Assertions.assertEquals(task, savedTask);
    }

    @Test
    public void canCreateSubtaskAndFindById() {
        Epic epic = new Epic("Приготовить ужин", "Салат+чай", Status.NEW, null);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Сделать салат", "Греческй салат", Status.NEW, 1);
        taskManager.createSubtask(subtask);
        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(1, subtasks.size());
        int id = subtask.getId();
        Subtask savedSubtask = taskManager.getSubtask(id);
        Assertions.assertEquals(subtask, savedSubtask);
    }

    @Test
    public void canCreateEpicAndFindById() {
        Epic epic = new Epic("Приготовить ужин", "Салат+чай", Status.NEW, null);
        taskManager.createEpic(epic);
        ArrayList<Epic> epics = taskManager.getEpics();
        Assertions.assertEquals(1, epics.size());
        int id = epic.getId();
        Epic savedEpic = taskManager.getEpic(id);
        Assertions.assertEquals(epic, savedEpic);
    }

    @Test
    public void canCreateTasksWithGivenIdAndGeneratedId() {
        Task task1 = new Task(1, "name1", "desc1", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("name2", "desc2", Status.NEW);
        taskManager.createTask(task2);
        ArrayList<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertTrue(task1.getId() != task2.getId());
    }

    @Test
    public void doesNotChangeTaskWhenAddingtoTaskManager() {
        Task task = new Task("name", "desc", Status.NEW);
        taskManager.createTask(task);
        Task savedTask = taskManager.getTask(task.getId());
        Assertions.assertTrue(task.getName() == savedTask.getName() && task.getDescription() == savedTask.getDescription() && task.getStatus() == savedTask.getStatus());
    }

    @Test
    public void checkEpicDoesNotHaveRemovedSubtusk() {
        Epic epic = new Epic("epic", "desc_epic", Status.NEW, null);
        Epic savedEpic = taskManager.createEpic(epic);
        Subtask sub1 = new Subtask("subtask1", "desc_sub1", Status.NEW, savedEpic.getId());
        Subtask sub2 = new Subtask("subtask2", "desc_sub2", Status.NEW, savedEpic.getId());
        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);
        List<Integer> list1 = epic.getSubtaskId();
        Assertions.assertEquals(2, list1.size());
        taskManager.deleteSubtask(sub2.getId());
        List<Integer> list2 = epic.getSubtaskId();
        Assertions.assertEquals(1, list2.size());
    }
}
