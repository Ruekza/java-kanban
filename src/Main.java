import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static manager.FileBackedTaskManager.loadFromFile;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task1);
        Epic epic2 = new Epic("epic2", "desc2", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("sub3", "desc3", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 2);
        Subtask subtask4 = new Subtask("sub4", "desc4", Status.NEW, LocalDateTime.of(2025, 8, 01, 15, 00), Duration.ofMinutes(240), 2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        Task task5 = new Task(task1.getId(), "task5", "desc5", Status.NEW, LocalDateTime.of(2025, 9, 23, 10, 30), Duration.ofMinutes(90));
        taskManager.updateTask(task5);
        Subtask subtask7 = new Subtask(4, "sub4", "desc4", Status.IN_PROGRESS, LocalDateTime.of(2025, 10, 23, 10, 35), Duration.ofMinutes(240), 2);
        taskManager.updateSubtask(subtask7);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getPrioritizedTasks());

        System.out.println("Проверяем загрузку задач из файла в менеджер tm");
        Path path = Paths.get("fileTest");
        TaskManager tm = loadFromFile(path.toFile());
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubtasks());
        System.out.println(tm.getPrioritizedTasks());

    }
}