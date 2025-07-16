package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static manager.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    public FileBackedTaskManagerTest() throws IOException {
    }

    @Test
    public void saveAndLoadEmptyFile() {
        System.out.println("Сохранение и загрузка пустого файла:");
        File file = new File("fileForTest");
        if (file.length() == 0) {
            System.out.println("Файл пустой");
        }
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        fb.save();
        loadFromFile(file);
        Assertions.assertEquals(0, fb.getTasks().size(), "Есть задачи");
        Assertions.assertEquals(0, fb.getEpics().size(), "Есть эпики");
        Assertions.assertEquals(0, fb.getSubtasks().size(), "Есть подзадачи");
        System.out.println(" ");
    }

    @Test
    public void saveTasksToFile() {
        System.out.println("Сохранение нескольких задач в файл:");
        File file = new File("fileForTest");
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        Task task = new Task("task", "descTask", Status.NEW);
        fb.createTask(task);
        Epic epic = new Epic("epic", "decsEpic", Status.DONE, null);
        fb.createEpic(epic);
        Assertions.assertNotNull(file, "Файл пустой");
        System.out.println(" ");
    }

    @Test
    public void loadTasksFromFile() {
        System.out.println("Загрузка нескольких задач из файла:");
        File file = new File("C:/Users/User", "data1.csv");
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        // Менеджер пустой до загрузки:
        Assertions.assertEquals(0, fb.getTasks().size());
        Assertions.assertEquals(0, fb.getEpics().size());
        Path path = Paths.get("fileForTest");
        TaskManager tm = loadFromFile(path.toFile());
        // Менеджер содержит задачи после загрузки:
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        Assertions.assertEquals(1, tm.getTasks().size());
        Assertions.assertEquals(1, tm.getEpics().size());
        System.out.println(" ");
    }
}
