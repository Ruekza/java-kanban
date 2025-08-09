package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = BaseHttpHandler.getGson();
    HttpServer httpServer;

    public SubtasksHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop(2);
        System.out.println("HTTP-сервер остановлен на 8080 порту!");
    }

    @Test
    public void checkCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertNotNull(subtasks, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("sub", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void checkGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertNotNull(subtasks, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("sub", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void checkUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask);
        Subtask subtask1 = new Subtask(2, "subNew", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        String subtaskJson = gson.toJson(subtask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertNotNull(subtasks, "Подзадачи не возвращаются");
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("subNew", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void checkDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(0, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertTrue(subtasks.isEmpty(), "Список подзадач должен быть пустым после удаления подзадачи");
    }

    @Test
    public void checkGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("sub1", "desc1", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        Subtask subtask2 = new Subtask("sub2", "desc2", Status.NEW, LocalDateTime.of(2025, 6, 30, 22, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertNotNull(subtasks, "Подзадачи не возвращаются");
        Assertions.assertEquals(2, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals("sub1", subtasks.get(0).getName(), "Некорректное имя подзадачи1");
        Assertions.assertEquals("sub2", subtasks.get(1).getName(), "Некорректное имя подзадачи2");
    }

    @Test
    public void checkStatusCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/8");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals(2, subtasks.get(0).getId(), "Некорректный id подзадачи");
        Assertions.assertNotEquals(8, subtasks.get(0).getId(), "Подзадачи с id=8 не должно быть");
    }

    @Test
    public void checkStatusCode406() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("sub1", "desc1", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        Subtask subtask2 = new Subtask("sub2", "desc2", Status.NEW, LocalDateTime.of(2025, 6, 28, 13, 15), Duration.ofMinutes(240), 1);
        Subtask createdSubtask1 = taskManager.createSubtask(subtask1);
        String taskJson = gson.toJson(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
        List<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        Assertions.assertEquals(2, subtasks.get(0).getId(), "Подзадача с id=3 не должна была создаться");
        Assertions.assertTrue(createdSubtask1.getId() == subtasks.get(0).getId());
    }

}
