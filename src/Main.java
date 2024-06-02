import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

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

        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW,
                LocalDateTime.now().minusYears(3), Duration.ofMinutes(15));
        int taskId1 = managerOrig.addNewTask(task1);
        Task task2 = new Task("Task_2", "tasks.Task 2 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(5), Duration.ofMinutes(35));
        int taskId2 = managerOrig.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        int epicId1 = managerOrig.addNewEpic(epic1);
        Epic epic2 = new Epic("Epic_2", "epic.Epic 2 description", TaskStatus.NEW);
        int epicId2 = managerOrig.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                epicId1, TaskStatus.NEW, LocalDateTime.now().minusMinutes(100), Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                epicId1, TaskStatus.NEW, LocalDateTime.now().minusMonths(6), Duration.ofMinutes(20));
        Subtask subtask3 = new Subtask("Subtask_3","subtask.Subtask 3 description",
                epicId1, TaskStatus.NEW, LocalDateTime.now().minusWeeks(9), Duration.ofMinutes(5));
        int subtaskId1 = managerOrig.addNewSubtask(subtask1);
        int subtaskId2 = managerOrig.addNewSubtask(subtask2);
        int subtaskId3 = managerOrig.addNewSubtask(subtask3);

        System.out.println("История просмотров пуста: " + managerOrig.getHistory().isEmpty());

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
        System.out.println("Подзадачи равны: " + managerOrig.getSubtasks().equals(managerNew.getSubtasks()));

        /*
        System.out.println("Статус эпика, когда все подзадачи New: " + epic1.getStatus());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        managerOrig.updateSubtask(subtask1);
        System.out.println("Статус эпика, когда есть подзадача в статусе IN_PROGRESS: " + epic1.getStatus());
        subtask1.setStatus(TaskStatus.DONE);
        managerOrig.updateSubtask(subtask1);
        System.out.println("Статус эпика, когда есть подзадача в статусе DONE: " + epic1.getStatus());
        subtask2.setStatus(TaskStatus.DONE);
        managerOrig.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        managerOrig.updateSubtask(subtask3);
        System.out.println("Статус эпика, когда все подзадачи DONE: " + epic1.getStatus());
*/

    }
}
