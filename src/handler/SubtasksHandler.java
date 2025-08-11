package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static handler.HttpMethod.*;
import static handler.ResponseCode.CREATED;
import static handler.ResponseCode.INTERNAL_SERVER_ERROR;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public SubtasksHandler() throws IOException {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String response;
            switch (method) {
                case GET: {
                    String[] parts = path.split("/");
                    if (parts.length > 2) {
                        String idStr = parts[2];
                        int id = parsePathId(idStr);
                        if (id != -1) {
                            try {
                                response = gson.toJson(taskManager.getSubtask(id));
                                sendText(exchange, response);
                            } catch (Exception e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        response = gson.toJson(taskManager.getSubtasks());
                        sendText(exchange, response);
                    }
                    break;
                }
                case POST: {
                    InputStream inputStream = exchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(jsonString, Subtask.class);
                    if (subtask != null) {
                        if (subtask.getId() == null || subtask.getId() == 0) {
                            try {
                                taskManager.createSubtask(subtask);
                                exchange.sendResponseHeaders(CREATED, 0);
                            } catch (IllegalArgumentException e) {
                                sendHasOverlaps(exchange);
                            }
                        } else {
                            try {
                                taskManager.updateSubtask(subtask);
                                exchange.sendResponseHeaders(CREATED, 0);
                            } catch (IllegalArgumentException e) {
                                sendHasOverlaps(exchange);
                            }
                        }
                    } else {
                        System.out.println("Подзадача не была успешно десериализована");
                    }
                    break;
                }
                case DELETE: {
                    String[] parts = path.split("/");
                    if (parts.length > 2) {
                        String idStr = parts[2];
                        int id = parsePathId(idStr);
                        if (id != -1) {
                            try {
                                response = gson.toJson(taskManager.deleteSubtask(id));
                                sendText(exchange, response);
                            } catch (Exception e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                        break;
                    }
                }
                default: {
                    exchange.sendResponseHeaders(INTERNAL_SERVER_ERROR, 0);
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(INTERNAL_SERVER_ERROR, 0);
        } finally {
            exchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
