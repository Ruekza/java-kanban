package manager;

import java.io.File;
import java.io.IOException;

public class Managers {

    private Managers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static TaskManager getDefault() throws IOException {
        return new FileBackedTaskManager(new File("fileTest"));
//        return new InMemoryTaskManager();
    }


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
