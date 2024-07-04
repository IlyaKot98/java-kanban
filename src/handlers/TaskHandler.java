package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.CreateException;
import manager.FileBackedTaskManager;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private FileBackedTaskManager manager;

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public TaskHandler (FileBackedTaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(), httpExchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(httpExchange);
                break;
            }
            case GET_TASKS_ID: {
                handleGetTasksId(httpExchange);
                break;
            }
            case POST_TASKS: {
                handlePostTasks(httpExchange);
                break;
            }
            case POST_TASKS_ID: {
                handlePostTasksId(httpExchange);
                break;
            }
            case DELETE_TASKS_ID: {
                handleDeleteTask(httpExchange);
                break;
            }
            case GET_EPICS: {
                handleGetEpics(httpExchange);
                break;
            }
            case GET_EPICS_ID: {
                handleGetEpicsId(httpExchange);
                break;
            }
            case GET_EPICS_ID_SUBTASKS: {
                handleGetEpicsIdSubtasks(httpExchange);
                break;
            }
            case POST_EPICS: {
                handlePostEpics(httpExchange);
                break;
            }
            case POST_EPICS_ID: {
                handlePostEpicsId(httpExchange);
                break;
            }
            case DELETE_EPICS_ID: {
                handleDeleteEpic(httpExchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtasks(httpExchange);
                break;
            }
            case GET_SUBTASKS_ID: {
                handleGetSubtasksId(httpExchange);
                break;
            }
            case POST_SUBTASKS: {
                handlePostSubtasks(httpExchange);
                break;
            }
            case POST_SUBTASKS_ID: {
                handlePostSubtasksId(httpExchange);
                break;
            }
            case DELETE_SUBTASKS_ID: {
                handleDeleteSubtask(httpExchange);
                break;
            }
            default:
                sendNotFound(httpExchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String [] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASKS;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASKS_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASKS_ID;
            }
        } else if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPICS;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPICS_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPICS_ID;
            }
        } else if (pathParts.length == 4 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS_ID_SUBTASKS;
            }
        } if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASKS;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS_ID;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASKS_ID;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASKS_ID;
            }
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTasks (HttpExchange exchange) {
        try {
            sendText(exchange, Optional.of(gson.toJson(manager.getTasks())));
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetTasksId (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdTask = getId(exchange);
        if (optIdTask.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        Task task;

        try {
            task = manager.getTask(optIdTask.get());

            if (task == null) throw new NullPointerException();

            String text = gson.toJson(task);
            sendText(exchange, Optional.of(text));
        } catch (NullPointerException exception) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handlePostTasks (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Task task = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Task.class);

        try {
            int id = manager.addNewTask(task);
            if (id == 0) throw new CreateException();
            sendText(exchange, Optional.empty());
        } catch (CreateException createException) {
            sendHasInteractions(exchange);
        }
    }

    private void handlePostTasksId (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Task task = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Task.class);

        Optional<Integer> optIdTask = getId(exchange);
        if (optIdTask.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            manager.updateTask(task);
            sendText(exchange, Optional.empty());
        } catch (CreateException createException) {
            sendHasInteractions(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleDeleteTask (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdTask = getId(exchange);
        if (optIdTask.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            Task task = manager.getTask(optIdTask.get());
            if (task == null) throw new  NullPointerException();

            manager.removeTask(optIdTask.get());
            sendText(exchange, Optional.of("Задача успешно удалена!"));
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpics (HttpExchange exchange) {
        try {
            sendText(exchange, Optional.of(gson.toJson(manager.getEpics())));
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpicsId (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdEpic = getId(exchange);
        if (optIdEpic.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        Epic epic;

        try {
            epic = manager.getEpic(optIdEpic.get());

            if (epic == null) throw new NullPointerException();

            String text = gson.toJson(epic);
            sendText(exchange, Optional.of(text));
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetEpicsIdSubtasks (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdEpic = getId(exchange);
        if (optIdEpic.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            Optional<String> text = Optional.of(gson.toJson(manager.getSubtasksEpic(optIdEpic.get())));
            sendText(exchange,text);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handlePostEpics (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Epic epic = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Epic.class);

        try {
            int id = manager.addNewEpic(epic);
            if (id == 0) throw new CreateException();
            sendText(exchange, Optional.empty());
        } catch (CreateException createException) {
            sendHasInteractions(exchange);
        }
    }

    private void handlePostEpicsId (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Epic epic = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Epic.class);

        Optional<Integer> optIdEpic = getId(exchange);
        if (optIdEpic.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            manager.updateEpic(epic);
            sendText(exchange, Optional.empty());
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleDeleteEpic (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdEpic = getId(exchange);
        if (optIdEpic.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            Epic epic = manager.getEpic(optIdEpic.get());
            if (epic == null) throw new NullPointerException();

            manager.removeEpic(optIdEpic.get());
            sendText(exchange,Optional.of("Success"));
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetSubtasks (HttpExchange exchange) {
        try {
            sendText(exchange, Optional.of(gson.toJson(manager.getSubtasks())));
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleGetSubtasksId (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdSub = getId(exchange);
        if (optIdSub.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        Subtask subtask;

        try {
            subtask = manager.getSubtask(optIdSub.get());

            if (subtask == null) throw new NullPointerException();

            String text = gson.toJson(subtask);
            sendText(exchange, Optional.of(text));
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handlePostSubtasks (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Subtask subtask = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Subtask.class);
        try {
            int id = manager.addNewSubtask(subtask);
            if (id == 0) throw new CreateException();
            sendText(exchange, Optional.empty());
        } catch (CreateException createException) {
            sendHasInteractions(exchange);
        }
    }

    private void handlePostSubtasksId (HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Subtask subtask = gson.fromJson(new String(inputStream.readAllBytes(), DEFAULT_CHARSET), Subtask.class);

        Optional<Integer> optIdTask = getId(exchange);
        if (optIdTask.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            manager.updateSubtask(subtask);
            sendText(exchange, Optional.empty());
        } catch (CreateException createException) {
            sendHasInteractions(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private void handleDeleteSubtask (HttpExchange exchange) throws IOException {
        Optional<Integer> optIdSub = getId(exchange);
        if (optIdSub.isEmpty()) {
            sendServerError(exchange);
            return;
        }

        try {
            Subtask subtask = manager.getSubtask(optIdSub.get());
            if (subtask == null) throw new NullPointerException();

            manager.removeSubtask(optIdSub.get());
            sendText(exchange,Optional.of("Success"));
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private Optional<Integer> getId (HttpExchange exchange) {
        String [] id = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(id[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}