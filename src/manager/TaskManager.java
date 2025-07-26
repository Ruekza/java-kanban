package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    public boolean isCrossing(Task t1, Task t2);

    // МЕТОДЫ ДЛЯ ЗАДАЧИ

    ArrayList<Task> getTasks();

    Task createTask(Task task);

    Task updateTask(Task task);

    Task deleteTask(Integer id);

    Task getTask(Integer id);

    void deleteAllTasks();

    // МЕТОДЫ ДЛЯ ЭПИКА
    ArrayList<Epic> getEpics();

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    Epic deleteEpic(Integer id);

    Epic getEpic(Integer id);

    void deleteAllEpics();

    // МЕТОДЫ ДЛЯ ПОДЗАДАЧ

    ArrayList<Subtask> getSubtasks();

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    Subtask deleteSubtask(Integer id);

    Subtask getSubtask(Integer id);

    ArrayList<Subtask> getAllSubtasksOfEpic(Integer epicId);

    void deleteAllSubtasks();
}
