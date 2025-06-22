import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


public class Main {

    public static void main(String[] args) {
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

        System.out.println("Создание задач, эпиков, подзадач");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println();

        // запрашиваем созданные задачи в разном порядке
        taskManager.getTask(2);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());

        taskManager.getTask(1);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());

        taskManager.getEpic(4);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());

        taskManager.getEpic(3);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());

        taskManager.getTask(2);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());
        System.out.println();

        // удалеляем задачу и проверяем, что ее нет в истории тоже
        taskManager.deleteTask(2);
        System.out.println("Просмотр списка историй после удаления задачи 2");
        System.out.println(taskManager.getHistory());
        System.out.println();

        // удаляем эпик с тремя подзадачами и проверяем, что его нет в истории тоже
        taskManager.deleteEpic(4);
        System.out.println("Просмотр списка историй после удаления эпика 4'");
        System.out.println(taskManager.getHistory());
    }
}