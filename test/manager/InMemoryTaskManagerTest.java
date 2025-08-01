package manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;


public class InMemoryTaskManagerTest extends TaskManagerTest {

    TaskManager taskManager = createTaskManager();

    protected InMemoryTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
    }


    @Override
    protected TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }


}
