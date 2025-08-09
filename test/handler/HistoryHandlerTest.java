package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
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

public class HistoryHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = BaseHttpHandler.getGson();
    HttpServer httpServer;

    public HistoryHandlerTest() throws IOException {
    }

    @Test
    public void checkGetHistory() throws IOException, InterruptedException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
        Task task1 = new Task("task1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 23, 10, 12), Duration.ofMinutes(90));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 23, 18, 50), Duration.ofMinutes(90));
        taskManager.createTask(task2);
        taskManager.getTask(1);
        taskManager.getTask(2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Task> history = taskManager.getHistory();
        Assertions.assertNotNull(history, "Задачи не возвращаются");
        Assertions.assertEquals(2, history.size(), "Некорректное количество задач");
        Assertions.assertEquals("task1", history.get(0).getName(), "Некорректное имя задачи1");
        Assertions.assertEquals("task2", history.get(1).getName(), "Некорректное имя задачи2");
        httpServer.stop(2);
        System.out.println("HTTP-сервер остановлен на 8080 порту!");
    }

}
