import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        // создаем задачи и эпики с подзадачами
        Task task1 = new Task("task1", "desc1", Status.NEW);
        taskManager.createTask(task1);

        Task task2 = new Task("task2", "desc2", Status.NEW);
        taskManager.createTask(task2);

        Epic epic3 = new Epic("epic3", "desc3", Status.NEW, null);
        taskManager.createEpic(epic3);

        Epic epic4 = new Epic("epic4", "desc4", Status.NEW, null);
        taskManager.createEpic(epic4);

        Subtask subtask5 = new Subtask("sub5", "desc5", Status.NEW, 4);
        taskManager.createSubtask(subtask5);

        Subtask subtask6 = new Subtask("sub6", "desc6", Status.NEW, 4);
        taskManager.createSubtask(subtask6);

        Subtask subtask7 = new Subtask("sub7", "desc7", Status.NEW, 4);
        taskManager.createSubtask(subtask7);

        System.out.println("Создание задач, эпиков, подзадач в памяти менеджера");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();
        File createdFile = new File("fileTest");
        String content = Files.readString(Paths.get(createdFile.getAbsolutePath()));
        System.out.println("Содержимое файла:");
        System.out.println(content);

        /*System.out.println("Проверяем загрузку задач из файла в менеджер");
        File file = new File("C:/Users/User", "data1.csv");
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        Path path = Paths.get("fileTest");
        TaskManager tm = fb.loadFromFile(path.toFile());
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubtasks());*/

    }
}