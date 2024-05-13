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

        FileBackedTaskManager managerOrig = FileBackedTaskManager.loadFromFile(file);

        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW);
        int taskId1 = managerOrig.addNewTask(task1);
        Task task2 = new Task("Task_2", "tasks.Task 2 description", TaskStatus.IN_PROGRESS);
        int taskId2 = managerOrig.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        int epicId1 = managerOrig.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "epic.Epic 2 description", TaskStatus.NEW);
        int epicId2 = managerOrig.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epicId1, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                epicId1, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Subtask_3","subtask.Subtask 3 description",
                epicId1, TaskStatus.NEW);
        int subtaskId1 = managerOrig.addNewSubtask(subtask1);
        int subtaskId2 = managerOrig.addNewSubtask(subtask2);
        int subtaskId3 = managerOrig.addNewSubtask(subtask3);

        managerOrig.getEpic(epicId1);
        managerOrig.getSubtask(subtaskId2);
        managerOrig.getTask(taskId1);
        managerOrig.getSubtask(subtaskId3);
        managerOrig.getTask(taskId2);
        managerOrig.getSubtask(subtaskId1);
        managerOrig.getEpic(epicId2);
        managerOrig.getEpic(epicId1);

        FileBackedTaskManager managerNew = FileBackedTaskManager.loadFromFile(file);

        System.out.println("История равна: " + managerOrig.getHistory().equals(managerNew.getHistory()));
        System.out.println("Задачи равны: " + managerOrig.getTasks().equals(managerNew.getTasks()));
        System.out.println("Эпики равны: " + managerOrig.getEpics().equals(managerNew.getEpics()));
        System.out.println("Подзадачт равны: " + managerOrig.getSubtasks().equals(managerNew.getSubtasks()));
    }
}
