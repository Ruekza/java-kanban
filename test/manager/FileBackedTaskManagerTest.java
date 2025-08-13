package manager;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static manager.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    TaskManager taskManager = createTaskManager();

    @Override
    protected TaskManager createTaskManager() throws IOException {
        return Managers.getDefault();
    }


    public FileBackedTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
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
        Task task = new Task("task", "descTask", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        fb.createTask(task);
        Epic epic = new Epic("epic", "decsEpic", Status.DONE, LocalDateTime.of(2025, 11, 10, 23, 02), Duration.ofMinutes(45), null);
        fb.createEpic(epic);
        Assertions.assertNotNull(file, "Файл пустой");
        System.out.println(" ");
    }

    @Test
    public void loadTasksFromFile() {
        System.out.println("Загрузка нескольких задач из файла:");
        // Менеджер пустой до загрузки:
        File file = new File("fileForTest");
        FileBackedTaskManager fb = new FileBackedTaskManager(file);
        Assertions.assertEquals(0, fb.getTasks().size());
        Assertions.assertEquals(0, fb.getEpics().size());
        // Сохранение нескольких задач в файл:
        Task task = new Task("task", "descTask", Status.NEW, LocalDateTime.of(2025, 7, 26, 14, 02), Duration.ofMinutes(45));
        fb.createTask(task);
        Epic epic = new Epic("epic", "decsEpic", Status.DONE, LocalDateTime.of(2025, 11, 10, 23, 02), Duration.ofMinutes(45), null);
        fb.createEpic(epic);
        // Менеджер содержит задачи после загрузки:
        Path path = Paths.get("fileForTest");
        TaskManager tm = loadFromFile(path.toFile());
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        Assertions.assertEquals(1, tm.getTasks().size());
        Assertions.assertEquals(1, tm.getEpics().size());
        System.out.println(" ");
    }

    @Test
    public void testLoadFromFile_ManagerSaveException1() {
        File nonExistentFile = new File("non-existent-file.csv");
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        }, "попытка загрузки несуществующего файла должны приводить к исключению");
    }

    @Test
    public void testLoadFromFile_ManagerSaveException2() {
        Path path = Paths.get("fileForTest");
        Assertions.assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(path.toFile());
        });
    }

    @Test
    public void testSave_ManagerSaveException() {
        File file = new File("path/to/non-writable-file.txt");
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
            taskManager.save();
        });
    }

}
