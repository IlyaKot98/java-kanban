package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File file;
    FileBackedTaskManager fbtm;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("text", ".csv", new File("/Users/kotkov-il/Documents/test"));
        fbtm = new FileBackedTaskManager(file);
    }

    @Test
    public void shouldSaveEmptyFile() throws IOException {
        file = File.createTempFile("text", ".csv", new File("/Users/kotkov-il/Documents/test"));
        fbtm = new FileBackedTaskManager(file);
        fbtm.save();
        String str = Files.readString(file.toPath());
        assertTrue(str.isEmpty(), "Файл не пустой!");
        file.deleteOnExit();
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(file);
        }, "Ошибка загрузки файла!");
        file.deleteOnExit();
    }

    @Test
    void shouldSaveMultipleTasksPerFile() throws IOException {
        file = File.createTempFile("text", ".csv", new File("/Users/kotkov-il/Documents/test"));
        fbtm = new FileBackedTaskManager(file);

        Task task = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW);
        int taskId = fbtm.addNewTask(task);

        Epic epic = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        int epicId = fbtm.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epicId, TaskStatus.NEW);
        int subtaskId = fbtm.addNewSubtask(subtask);

        fbtm.getTask(taskId);
        fbtm.getSubtask(subtaskId);
        fbtm.getEpic(epicId);
        fbtm.getTask(taskId);

        fbtm.save();

        String str = Files.readString(file.toPath());
        assertTrue(!str.isEmpty(), "Файл пустой!");
        file.deleteOnExit();
    }

    @Test
    void shouldLoadMultipleTasksPerFile() throws IOException {
        file = File.createTempFile("text", ".csv", new File("/Users/kotkov-il/Documents/test"));
        fbtm = new FileBackedTaskManager(file);

        Task task = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW);
        int taskId = fbtm.addNewTask(task);

        Epic epic = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        int epicId = fbtm.addNewEpic(epic);

        Subtask subtask = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epicId, TaskStatus.NEW);
        int subtaskId = fbtm.addNewSubtask(subtask);

        fbtm.getTask(taskId);
        fbtm.getSubtask(subtaskId);
        fbtm.getEpic(epicId);
        fbtm.getTask(taskId);

        fbtm.save();

        String strActual = FileBackedTaskManager.loadFromFile(file).getHistory().toString();
        assertTrue(!strActual.isEmpty(), "Не удалось загрузить задачи из файла!");
        file.deleteOnExit();
    }
}