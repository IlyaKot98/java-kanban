import java.util.ArrayList;
import java.util.HashMap;
public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        int taskId = manager.addNewTask(new Task("Переезд","Описание задачи переезд",  "NEW"));
        manager.addNewTask(new Task("Путешествие", "Описание задачи путешествие", "IN_PROGRESS"));

        Epic epic1 = new Epic("Домашние дела","Описание задачи домашние дела", "NEW");
        Epic epic2 = new Epic("Отдых", "Описание задачи для отдыха", "IN_PROGRESS");
        Epic epic3 = new Epic("Работа", "Описание задачи для работы", "IN_PROGRESS");

        int epicId1 = manager.addNewEpic(epic1);
        int epicId2 = manager.addNewEpic(epic2);
        int epicId3 = manager.addNewEpic(epic3);

        manager.addNewSubtask(new Subtask("Сходить в магазин","Описание задачи магазин", "NEW", epicId1));
        int subtaskId = manager.addNewSubtask(new Subtask("Съездить на дачу","Описание задачи для дачи", "IN_PROGRESS", epicId1));
        manager.addNewSubtask(new Subtask("Приготовить шашлык","Описание задачи шашлыка", "DONE", epicId2));

        manager.test();

        System.out.println(manager.getSubtasksEpic(epicId1));

        manager.test();
    }
}
