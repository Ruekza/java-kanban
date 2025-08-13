package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
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

public class PrioritizedHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = BaseHttpHandler.getGson();
    HttpServer httpServer;

    public PrioritizedHandlerTest() throws IOException {
    }

    @Test
    public void checkGetHistory() throws IOException, InterruptedException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 23, 18, 50), Duration.ofMinutes(90));
        taskManager.createTask(task2);
        Epic epic3 = new Epic("epic3", "desc3", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic3);
        Subtask subtask4 = new Subtask("sub4", "desc4", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 3);
        taskManager.createSubtask(subtask4);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Assertions.assertNotNull(prioritizedTasks, "Задачи не возвращаются");
        Assertions.assertEquals(3, prioritizedTasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("sub4", prioritizedTasks.get(0).getName(), "Некорректное имя задачи на первом месте");
        Assertions.assertEquals("task1", prioritizedTasks.get(1).getName(), "Некорректное имя задачи на втором месте");
        Assertions.assertEquals("task2", prioritizedTasks.get(2).getName(), "Некорректное имя задачи на третьем месте");
        httpServer.stop(2);
        System.out.println("HTTP-сервер остановлен на 8080 порту!");
    }

}
