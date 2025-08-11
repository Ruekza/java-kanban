package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

import static handler.HttpMethod.GET;
import static handler.ResponseCode.INTERNAL_SERVER_ERROR;


public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public PrioritizedHandler() throws IOException {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            switch (method) {
                case GET: {
                    String[] parts = path.split("/");
                    String response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, response);

                    break;
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

}
