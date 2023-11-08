import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        Task task1 = new Task("Task_1","tasks.Task 1 description",  "NEW");
        Task task2 = new Task("Task_2", "tasks.Task 2 description", "IN_PROGRESS");
        int taskId1 = manager.addNewTask(task1);
        int taskId2 = manager.addNewTask(task2);

        Epic epic1 = new Epic("Домашние дела","Описание задачи домашние дела", "NEW");
        Epic epic2 = new Epic("Отдых", "Описание задачи для отдыха", "IN_PROGRESS");
        Epic epic3 = new Epic("Работа", "Описание задачи для работы", "IN_PROGRESS");
        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);
        int epicId3 = manager.addNewEpic(epic3);

        Subtask subtask1 = new Subtask("Сходить в магазин","Описание задачи магазин",
                "NEW", epicId1);
        Subtask subtask2 = new Subtask("Съездить на дачу","Описание задачи для дачи",
                "NEW", epicId1);
        Subtask subtask3 = new Subtask("Test_status_1","Описание задачи магазин",
                "NEW", epicId1);
        Subtask subtask4 = new Subtask("Test_status_2","Описание задачи магазин",
                "IN_PROGRESS", epicId1);
        Subtask subtask5 = new Subtask("Test_status_3","Описание задачи магазин",
                "DONE", epicId1);
        Subtask subtask6 = new Subtask("Приготовить шашлык","Описание задачи шашлыка",
                "DONE", epicId2);
        int subtaskId1 = manager.addNewSubtask(subtask1);
        int subtaskId2 = manager.addNewSubtask(subtask2);
        int subtaskId3 = manager.addNewSubtask(subtask3);
        int subtaskId4 = manager.addNewSubtask(subtask4);
        int subtaskId5 = manager.addNewSubtask(subtask5);
        int subtaskId6 = manager.addNewSubtask(subtask6);

        System.out.println(manager.getSubtasksEpic(epicId1));

    }
}
