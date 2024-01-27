import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task_1","tasks.Task 1 description", TaskStatus.NEW);
        Task task2 = new Task("Task_2", "tasks.Task 2 description", TaskStatus.NEW);
        int taskId1 = manager.addNewTask(task1);
        int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
        Epic epic2 = new Epic("Epic_2", "epic.Epic 2 description", TaskStatus.NEW);

        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);


        Subtask subtask1 = new Subtask("Subtask_1","subtask.Subtask 1 description",
                 epicId1, TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask_2","subtask.Subtask 2 description",
                 epicId1, TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Subtask_3","subtask.Subtask 3 description",
                 epicId1, TaskStatus.NEW);
        Subtask subtask4 = new Subtask("Subtask_4","subtask.Subtask 4 description",
                 epicId1, TaskStatus.NEW);
        Subtask subtask5 = new Subtask("Subtask_5","subtask.Subtask 5 description",
                 epicId1, TaskStatus.NEW);

        int subtaskId1 = manager.addNewSubtask(subtask1);
        int subtaskId2 = manager.addNewSubtask(subtask2);
        int subtaskId3 = manager.addNewSubtask(subtask3);
        int subtaskId4 = manager.addNewSubtask(subtask4);
        int subtaskId5 = manager.addNewSubtask(subtask5);


        System.out.println("Список задач:");
        System.out.println(manager.getTasks());
        System.out.println("Список эпиков:");
        System.out.println(manager.getEpics());
        System.out.println("Список подзадач:");
        System.out.println(manager.getSubtasks());

        System.out.println("История просмотренных задач:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println(manager.getHistory().get(i));
        }

        manager.getTask(taskId2);
        manager.getSubtask(subtaskId1);
        manager.getTask(taskId1);
        manager.getTask(taskId2);
        manager.getSubtask(subtaskId1);

        System.out.println("История просмотренных задач:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println(manager.getHistory().get(i));
        }

        manager.getTask(taskId2);
        manager.getEpic(epicId2);
        manager.getTask(taskId1);
        manager.getSubtask(subtaskId5);
        manager.getSubtask(subtaskId1);
        manager.getTask(taskId2);
        manager.getSubtask(subtaskId1);
        manager.getSubtask(subtaskId2);
        manager.getEpic(epicId2);
        manager.getSubtask(subtaskId3);
        manager.getTask(taskId1);
        manager.getSubtask(subtaskId4);
        manager.getSubtask(subtaskId5);
        manager.getEpic(epicId1);
        manager.getEpic(epicId2);
        manager.getEpic(epicId1);

        System.out.println("История просмотренных задач:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println(manager.getHistory().get(i));
        }

        manager.removeTask(taskId1);
        manager.removeSubtask(subtaskId1);

        System.out.println("История просмотренных задач:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println(manager.getHistory().get(i));
        }

        manager.removeEpic(epicId1);

        System.out.println("История просмотренных задач:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println(manager.getHistory().get(i));
        }
    }
}
