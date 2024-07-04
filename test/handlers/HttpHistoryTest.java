package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
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

class HistoryListTypeToken extends TypeToken<List<Task>> {}

public class HttpHistoryTest {

    FileBackedTaskManager manager = new FileBackedTaskManager(new File("test.csv"));
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    HttpTaskServer httpTaskServer = new HttpTaskServer(manager);

    public HttpHistoryTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        manager.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "epic.Epic 2 description", TaskStatus.NEW);
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epic2.getId(), TaskStatus.NEW, LocalDateTime.now().minusMinutes(100).withSecond(0).withNano(0), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                epic2.getId(), TaskStatus.NEW, LocalDateTime.now().minusMonths(6).withSecond(0).withNano(0), Duration.ofMinutes(20));
        manager.addNewSubtask(subtask1);
        manager.addNewSubtask(subtask2);

        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW,
                LocalDateTime.now().minusYears(3).withSecond(0).withNano(0), Duration.ofMinutes(15));
        manager.addNewTask(task1);
        Task task2 = new Task("Task_2", "tasks.Task 2 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(5).withSecond(0).withNano(0), Duration.ofMinutes(35));
        manager.addNewTask(task2);

        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask2.getId());
        manager.getTask(task1.getId());
        manager.getSubtask(subtask1.getId());
        manager.getTask(task2.getId());
        manager.getSubtask(subtask2.getId());
        manager.getEpic(epic2.getId());
        manager.getEpic(epic1.getId());

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
    public void testGetHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> historyFromManager = manager.getHistory();
        List<Task> historyFromResponse = gson.fromJson(response.body(), new HistoryListTypeToken().getType());

        assertNotNull(historyFromManager, "История не возвращается");
        assertNotNull(historyFromResponse, "История не получена из тела ответа");
        assertEquals(historyFromManager.size(), historyFromResponse.size(), "Некорректное количество задач");
    }
}
