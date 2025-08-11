package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static handler.ResponseCode.*;

public class BaseHttpHandler {
    protected static Gson gson = getGson();

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(OK, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        String text = "По указанному id задачи нет";
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(NOT_FOUND, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        String text = "Задача пересекается с другими";
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(NOT_ACCEPTABLE, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}

