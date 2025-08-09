package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class TasksHandlerTest {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = BaseHttpHandler.getGson();
    HttpServer httpServer;

    public TasksHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop(2);
        System.out.println("HTTP-сервер остановлен на 8080 порту!");
    }

    @Test
    public void checkCreateTask() throws IOException, InterruptedException {
        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void checkGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("task", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void checkUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task);
        Task task1 = new Task(1, "taskNew", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        String taskJson = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("taskNew", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void checkDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(0, tasks.size(), "Некорректное количество задач");
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пустым после удаления задачи");
    }

    @Test
    public void checkGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи1");
        Assertions.assertEquals("task2", tasksFromManager.get(1).getName(), "Некорректное имя задачи2");
    }

    @Test
    public void checkStatusCode404() throws IOException, InterruptedException {
        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/8");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(1, tasksFromManager.get(0).getId(), "Некорректный id задачи");
        Assertions.assertNotEquals(8, tasksFromManager.get(0).getId(), "Задачи с id=8 не должно быть");
    }

    @Test
    public void checkStatusCode406() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        Task createdTask1 = taskManager.createTask(task1);
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 7, 23, 11, 00), Duration.ofMinutes(90));
        String taskJson = gson.toJson(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals(1, tasksFromManager.get(0).getId(), "Задача с id=2 не должна была создаться");
        Assertions.assertTrue(createdTask1.getId() == tasksFromManager.get(0).getId());
    }

}
