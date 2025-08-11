package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static handler.HttpMethod.*;
import static handler.ResponseCode.CREATED;
import static handler.ResponseCode.INTERNAL_SERVER_ERROR;


public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public EpicsHandler() throws IOException {
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
                    if (parts.length == 3) {
                        String idStr = parts[2];
                        int id = parsePathId(idStr);
                        if (id != -1) {
                            try {
                                response = gson.toJson(taskManager.getEpic(id));
                                sendText(exchange, response);
                            } catch (Exception e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if (parts.length == 4) {
                        String idStr = parts[2];
                        int id = parsePathId(idStr);
                        if (id != -1) {
                            try {
                                response = gson.toJson(taskManager.getAllSubtasksOfEpic(id));
                                sendText(exchange, response);
                            } catch (Exception e) {
                                sendNotFound(exchange);
                            }
                        } else {
                            sendNotFound(exchange);
                        }
                    } else {
                        response = gson.toJson(taskManager.getEpics());
                        sendText(exchange, response);
                    }
                    break;
                }
                case POST: {
                    InputStream inputStream = exchange.getRequestBody();
                    String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(jsonString, Epic.class);
                    if (epic != null) {
                        if (epic.getId() == null || epic.getId() == 0) {
                            try {
                                taskManager.createEpic(epic);
                                exchange.sendResponseHeaders(CREATED, 0);
                            } catch (IllegalArgumentException e) {
                                sendHasOverlaps(exchange);
                            }
                        } else {
                            try {
                                taskManager.updateEpic(epic);
                                exchange.sendResponseHeaders(CREATED, 0);
                            } catch (IllegalArgumentException e) {
                                sendHasOverlaps(exchange);
                            }
                        }
                    } else {
                        System.out.println("Эпик не был успешно десериализован");
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
                                response = gson.toJson(taskManager.deleteEpic(id));
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

