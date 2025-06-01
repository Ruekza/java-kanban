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

        // Тестирование
        // Создание задач, эпиков, подзадач:

        // Создание задачи
        Task task1 = new Task("Похудеть до конца года", "Сбросить 10 кг", Status.NEW);
        taskManager.createTask(task1);

        // Создание задачи
        Task task2 = new Task("Купить соковыжималку", "Moulinex, центробежная", Status.NEW);
        taskManager.createTask(task2);

        // Создание эпика
        Epic epic1 = new Epic("Запланировать отпуск", "Всё подготовить к 15 июня", Status.NEW, null);
        taskManager.createEpic(epic1);

        // Создание эпика
        Epic epic2 = new Epic("Отвезти кота к ветеринару", "Ежегодная прививка", Status.NEW, null);
        taskManager.createEpic(epic2);

        // Создание подзадачи
        Subtask subtask1 = new Subtask("Засунуть кота в переноску", "Достать переноску, поймать кота", Status.NEW, 4);
        taskManager.createSubtask(subtask1);

        // Создание подзадачи"
        Subtask subtask2 = new Subtask("Вызвать такси", "Заказать такси в приложении", Status.NEW, 4);
        taskManager.createSubtask(subtask2);

        // Создание подзадачи"
        Subtask subtask3 = new Subtask("Купить билеты, забронировать гостиницу", "Приложение авиасейлз", Status.NEW, 3);
        taskManager.createSubtask(subtask3);

        // Распечатываем списки задач, эпиков, подзадач:

        System.out.println("Создание задач, эпиков, подзадач");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();


        // Обновляем статусы задач, подзадач и эпиков (для эпиков рассчитываем):

        // Обновление задачи
        Task task3 = new Task(task1.getId(), "Похудеть до конца года", "Сбросить 10 кг", Status.IN_PROGRESS);
        taskManager.updateTask(task3);

        // Обновление задачи
        Task task4 = new Task(task2.getId(), "Купить соковыжималку", "Moulinex, центробежная", Status.DONE);
        taskManager.updateTask(task4);

        // Обновление подзадачи и эпика
        subtask1 = new Subtask(5, "Засунуть кота в переноску", "Достать переноску, поймать кота", Status.DONE, 4);
        taskManager.updateSubtask(subtask1);

        // Обновление подзадачи и эпика
        subtask3 = new Subtask(7, "Купить билеты, забронировать гостиницу", "Приложение авиасейлз", Status.DONE, 3);
        taskManager.updateSubtask(subtask3);

        System.out.println("Обновление статусов задач, подзадач, эпиков");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();


        // Удаление задачи и эпика:

        // Удаление задачи
        taskManager.deleteTask(task2.getId());

        // Удаление эпика
        taskManager.deleteEpic(epic2.getId());

        System.out.println("Удаление задачи и эпика");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println();

        // Просмотр списка историй
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(7);
        System.out.println("Просмотр списка историй");
        System.out.println(taskManager.getHistory());
    }
}