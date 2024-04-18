import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File( "test.csv");
        if(!Files.exists(file.toPath())){
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW);
        int taskId1 = manager.addNewTask(task1);

        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        int epicId1 = manager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epicId1, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                epicId1, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Subtask_3","subtask.Subtask 3 description",
                epicId1, TaskStatus.NEW);
        int subtaskId1 = manager.addNewSubtask(subtask1);
        int subtaskId2 = manager.addNewSubtask(subtask2);
        int subtaskId3 = manager.addNewSubtask(subtask3);

        manager.getEpic(epicId1);
        manager.getSubtask(subtaskId2);
        manager.getTask(taskId1);
        manager.getSubtask(subtaskId3);
    }
}
