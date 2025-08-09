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

public class EpicsHandlerTest {

    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = BaseHttpHandler.getGson();
    HttpServer httpServer;

    public EpicsHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на 8080 порту!");
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop(2);
        System.out.println("HTTP-сервер остановлен на 8080 порту!");
    }

    @Test
    public void checkCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 15), Duration.ofMinutes(50), null);
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertNotNull(epics, "Эпики не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("epic", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void checkGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertNotNull(epics, "Эпики не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("epic", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void checkUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic);
        Epic epic1 = new Epic(1, "epicNew", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        String epicJson = gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertNotNull(epics, "Эпики не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("epicNew", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void checkDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertEquals(0, epics.size(), "Некорректное количество эпиков");
        Assertions.assertTrue(epics.isEmpty(), "Список эпиков должен быть пустым после удаления эпика");
    }

    @Test
    public void checkGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "desc1", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        Epic epic2 = new Epic("epic2", "desc2", Status.NEW, LocalDateTime.of(2025, 8, 11, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertNotNull(epics, "Эпики не возвращаются");
        Assertions.assertEquals(2, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("epic1", epics.get(0).getName(), "Некорректное имя эпика1");
        Assertions.assertEquals("epic2", epics.get(1).getName(), "Некорректное имя эпика2");
    }

    @Test
    public void checkStatusCode404() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 00), Duration.ofMinutes(50), null);
        taskManager.createEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/8");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals(1, epics.get(0).getId(), "Некорректный id эпика");
        Assertions.assertNotEquals(8, epics.get(0).getId(), "Эпика с id=8 не должно быть");
    }

    @Test
    public void checkGetAllSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "desc", Status.NEW, LocalDateTime.of(2025, 7, 25, 10, 15), Duration.ofMinutes(50));
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("sub", "desc", Status.NEW, LocalDateTime.of(2025, 6, 28, 12, 15), Duration.ofMinutes(240), 1);
        taskManager.createSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Epic> epics = taskManager.getEpics();
        Assertions.assertNotNull(epics, "Эпики не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество эпиков");
        Assertions.assertEquals("epic", epics.get(0).getName(), "Некорректное имя эпика");
        Assertions.assertEquals(2, epics.get(0).getSubtaskId().get(0), "id подзадачи должен быть равен 2");
        Assertions.assertEquals(1, epics.get(0).getSubtaskId().size(), "Эпик содержит одну подзадачу");
    }

}
