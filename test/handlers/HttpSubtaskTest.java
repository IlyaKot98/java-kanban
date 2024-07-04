package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
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

class SubtaskListTypeToken extends TypeToken<List<Subtask>> {}

public class HttpSubtaskTest {

    FileBackedTaskManager manager = new FileBackedTaskManager(new File("test.csv"));
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    public HttpSubtaskTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "epic.Epic 2 description", TaskStatus.NEW);
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epic2.getId(), TaskStatus.NEW, LocalDateTime.now().minusMinutes(100), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                epic2.getId(), TaskStatus.NEW, LocalDateTime.now().minusMonths(6), Duration.ofMinutes(20));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

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
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask_Test","subtask.Subtask Test description",
                manager.getEpics().get(0).getId(), TaskStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(1000), Duration.ofMinutes(15));

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(3, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask_Test", subtasksFromManager.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask_Test","subtask.Subtask Test description",
                2, TaskStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(1000), Duration.ofMinutes(15));
        subtask.setId(3);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + 3);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Subtask subtaskOrig = manager.getSubtask(3);

        assertEquals(subtask.getDescription(), subtaskOrig.getDescription(), "Некорректное описание задачи");
        assertEquals(subtask.getStatus(), subtaskOrig.getStatus(), "Некорректный статус задачи");
        assertEquals(subtask.getDuration(), subtaskOrig.getDuration(), "Некорректное время исполнение задачи");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskEpicListTypeToken().getType());

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertNotNull(subtasksFromResponse, "Задачи не получены из тела ответа");
        assertEquals(subtasksFromManager.size(), subtasksFromResponse.size(), "Некорректное количество задач");
        assertEquals(subtasksFromManager, subtasksFromResponse, "Некорректный список задач в ответе");
    }

    @Test
    public void testGetSubtaskId() throws IOException, InterruptedException {
        Subtask subtaskOrig = manager.getSubtask(3);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + 3);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask subtaskFromJson = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtaskFromJson, "Задача не возвращаются");
        assertEquals(subtaskOrig, subtaskFromJson, "Некорректная задача из тела ответа");
    }

    @Test
    public void testGetSubtasksIdServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/asdasd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    public void testGetSubtasksIdErrorGetSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + 101);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        assertEquals(response.body(), "Not Found", "Некорректное тело ответ");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtaskOrig = manager.getSubtask(3);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + 3);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertFalse(subtasksFromManager.contains(subtaskOrig), "Задача не удалена");
    }

    @Test
    public void testDeleteSubtaskServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + "asd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteSubtaskErrorDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/subtasks/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
    }
}
