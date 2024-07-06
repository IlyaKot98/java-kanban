package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.FileBackedTaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserHandler extends BaseHttpHandler implements HttpHandler {

    private FileBackedTaskManager manager;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public UserHandler (FileBackedTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle (HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY: {
                handleGetHistory(exchange);
            }
            case GET_PRIORITIZED: {
                handleGetPrioritized(exchange);
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint (String requestPath, String requestMethod) {
        String [] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        } else if (pathParts.length == 2 && pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetHistory (HttpExchange exchange) {
        try {
            sendText(exchange, Optional.of(gson.toJson(manager.getHistory())));
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetPrioritized (HttpExchange exchange) {
        try {
            sendText(exchange, Optional.of(gson.toJson(manager.getPrioritizedTasks())));
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }
}
