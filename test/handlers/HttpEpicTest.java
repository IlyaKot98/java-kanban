package handlers;

import com.google.gson.*;
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

class EpicListTypeToken extends TypeToken<List<Epic>> {}

class SubtaskEpicListTypeToken extends TypeToken<List<Subtask>> {}

public class HttpEpicTest {

    FileBackedTaskManager manager = new FileBackedTaskManager(new File("test.csv"));
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    public HttpEpicTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic_Test","epic.Epic Test description", TaskStatus.NEW);

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(3, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Epic_Test", epicsFromManager.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic_Test","epic.Epic Test description", TaskStatus.NEW);
        epic.setId(1);

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Epic epicOrig = manager.getEpic(1);

        assertEquals(epic.getDescription(), epicOrig.getDescription(), "Некорректное описание задачи");
        assertEquals(epic.getStatus(), epicOrig.getStatus(), "Некорректный статус задачи");
        assertEquals(epic.getDuration(), epicOrig.getDuration(), "Некорректное время исполнение задачи");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();
        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertNotNull(epicsFromResponse, "Задачи не получены из тела ответа");
        assertEquals(epicsFromManager.size(), epicsFromResponse.size(), "Некорректное количество задач");
        assertEquals(epicsFromManager, epicsFromResponse, "Некорректный список задач в ответе");
    }

    @Test
    public void testGetEpicId() throws IOException, InterruptedException {
        Epic epicOrig = manager.getEpic(1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epicFromJson, "Задача не возвращаются");
        assertEquals(epicOrig, epicFromJson, "Некорректная задача из тела ответа");
    }

    @Test
    public void testGetEpicsIdServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/asdasd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    public void testGetEpicsIdErrorGetEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 10);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        assertEquals(response.body(), "Not Found", "Некорректное тело ответ");
    }

    @Test
    public void testGetEpicsIdSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 2 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksEpicFromManager = manager.getSubtasksEpic(2);
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskEpicListTypeToken().getType());

        assertNotNull(subtasksEpicFromManager, "Задачи не возвращаются");
        assertNotNull(subtasksFromResponse, "Задачи не получены из тела ответа");
        assertEquals(subtasksEpicFromManager.size(), subtasksFromResponse.size(), "Некорректное количество задач");
        assertEquals(subtasksEpicFromManager, subtasksFromResponse, "Некорректный список задач в ответе");
    }

    @Test
    public void testGetEpicsIdSubtasksServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + "sas" + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }

    @Test
    public void testGetEpicsIdSubtasksErrorGet() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 10101 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epicOrig = manager.getEpic(1);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        Assertions.assertFalse(epicsFromManager.contains(epicOrig), "Задача не удалена");
    }

    @Test
    public void testDeleteEpicServerError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + "asd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicErrorDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/epics/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(2, epicsFromManager.size(), "Некорректное количество задач");
    }
}
