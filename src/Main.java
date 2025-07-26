import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


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

        Task task5 = new Task("task5", "desc5", Status.NEW, LocalDateTime.of(2025, 11, 23, 10, 30), Duration.ofMinutes(90));
        taskManager.createTask(task5);
        Subtask subtask7 = new Subtask(4, "sub4", "desc4", Status.IN_PROGRESS, LocalDateTime.of(2025, 4, 23, 11, 00), Duration.ofMinutes(240), 2);
        taskManager.updateSubtask(subtask7);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getPrioritizedTasks());

    }
}