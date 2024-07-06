package handlers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskTest {

    FileBackedTaskManager manager = new FileBackedTaskManager(new File("test.csv"));
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    class TaskListTypeToken extends TypeToken<List<Task>> {}

    public HttpTaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW,
                LocalDateTime.now().minusYears(3), Duration.ofMinutes(15));
        manager.addNewTask(task1);
        Task task2 = new Task("Task_2", "tasks.Task 2 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(5), Duration.ofMinutes(35));
        manager.addNewTask(task2);

        try {
            httpTaskServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Task_Test","tasks.Task Test description", TaskStatus.NEW,
                LocalDateTime.now().minusYears(50), Duration.ofMinutes(150));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(3, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task_Test", tasksFromManager.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTasksErrorHasInteractions() throws IOException, InterruptedException {
        Task task = new Task("Task_Test","tasks.Task Test description", TaskStatus.NEW,
                LocalDateTime.now().minusYears(3), Duration.ofMinutes(150));

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Not Acceptable", response.body(), "Некорректное тело ответа");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task_2","tasks.Task Test description", TaskStatus.DONE,
                LocalDateTime.now().minusDays(5), Duration.ofMinutes(150));
        task.setId(2);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task taskOrig = manager.getTask(2);

        assertEquals(task.getDescription(), taskOrig.getDescription(), "Некорректное описание задачи");
        assertEquals(task.getStatus(), taskOrig.getStatus(), "Некорректный статус задачи");
        assertEquals(task.getDuration(), taskOrig.getDuration(), "Некорректное время исполнение задачи");
    }

    @Test
    public void testUpdateTasksErrorHasInteractions() throws IOException, InterruptedException {
        Task task = new Task("Task_2","tasks.Task Test description", TaskStatus.DONE,
                LocalDateTime.now().minusYears(3), Duration.ofMinutes(15));
        task.setId(2);

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + 2);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Not Acceptable", response.body(), "Некорректное тело ответа");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertNotNull(tasksFromResponse, "Задачи не получены из тела ответа");
        assertEquals(tasksFromManager.size(), tasksFromResponse.size(), "Некорректное количество задач");
        assertEquals(tasksFromManager, tasksFromResponse, "Некорректный список задач в ответе");
    }

    @Test
    public void testGetTaskId() throws IOException, InterruptedException {
        Task taskOrig = manager.getTask(1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task taskFromJson = gson.fromJson(response.body(), Task.class);

        assertNotNull(taskFromJson, "Задача не возвращаются");
        assertEquals(taskOrig, taskFromJson, "Некорректная задача из тела ответа");
    }

    @Test
    public void testGetTasksIdServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/asdasd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    public void testGetTasksIdErrorGetTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + 10);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        assertEquals(response.body(), "Not Found", "Некорректное тело ответ");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task taskOrig = manager.getTask(1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> taskFromManager = manager.getTasks();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество задач");
        Assertions.assertFalse(taskFromManager.contains(taskOrig), "Задача не удалена");
    }

    @Test
    public void testDeleteTaskServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + "asd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());

        List<Task> taskFromManager = manager.getTasks();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(2, taskFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskErrorDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Task> taskFromManager = manager.getTasks();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(2, taskFromManager.size(), "Некорректное количество задач");
    }
}
