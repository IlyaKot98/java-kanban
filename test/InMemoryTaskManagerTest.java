import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import java.util.List;

class InMemoryTaskManagerTest {

    TaskManager manager = Managers.getDefault();
    Task task = new Task("TestTask_1", "Test_Task_1_Description", TaskStatus.NEW);
    int idNewTask = manager.addNewTask(task);
    Epic epic = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
    int idNewEpic = manager.addNewEpic(epic);
    Subtask subtask = new Subtask("Subtask_1","subtask.Subtask 1 description", epic.getId(),
            TaskStatus.NEW);
    int idNewSubtask = manager.addNewSubtask(subtask);

    @Test
    void addNewTask() {
        final Task savedTask = manager.getTask(idNewTask);

        Assertions.assertNotNull(savedTask, "Задача не найдена!");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают!");

        final List<Task> tasks = manager.getTasks();

        Assertions.assertNotNull(tasks, "Задачи не загружены!");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач!");
        Assertions.assertEquals(task, tasks.get(0), "Задачи не совпадают!");
    }

    @Test
    void addNewSubtask() {
        final Subtask savedSubtask = manager.getSubtask(idNewSubtask);

        Assertions.assertNotNull(savedSubtask, "Подзадача не найдена!");
        Assertions.assertEquals(subtask, savedSubtask, "Подзадачи не совпадают!");

        final List<Subtask> subtasks = manager.getSubtasks();

        Assertions.assertNotNull(subtasks, "Подзадачи не загружены!");
        Assertions.assertEquals(1, subtasks.size(), "Неверное количество подзадач!");
        Assertions.assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают!");
    }

    @Test
    void addNewEpic() {
        final Epic savedEpic = manager.getEpic(idNewEpic);

        Assertions.assertNotNull(savedEpic, "Подзадача не найдена!");
        Assertions.assertEquals(epic, savedEpic, "Подзадачи не совпадают!");

        final List<Epic> epics = manager.getEpics();

        Assertions.assertNotNull(epic, "Подзадачи не загружены!");
        Assertions.assertEquals(1, epics.size(), "Неверное количество подзадач!");
        Assertions.assertEquals(epic, epics.get(0), "Подзадачи не совпадают!");
    }

    @Test
    void returnHistory() {
        manager.getTask(task.getId());
        manager.getSubtask(subtask.getId());
        manager.getEpic(epic.getId());

        List<Task> history = manager.getHistory();

        Assertions.assertNotNull(history, "История не загружена!");
        Assertions.assertEquals(3, history.size(),
                "Количество записей в истории, не совпадает с количеством задач!");
    }

    @Test
    void taskEqualsTask() {

        Assertions.assertNotNull(idNewTask, "Отсутствует id созданной задачи");
        Assertions.assertEquals(task, manager.getTask(idNewTask), "Задачи не равны между собой!");
    }

    @Test
    void heirsTaskEqualsTask() {
        Assertions.assertNotNull(idNewEpic, "Отсутствует id созданного эпика");
        Assertions.assertNotNull(idNewSubtask, "Отсутствует id созданной подзадачи");
        Assertions.assertEquals(epic, manager.getEpic(idNewEpic), "Эпики не равны между собой!");
        Assertions.assertEquals(subtask, manager.getSubtask(idNewSubtask), "Подзадачи не равны между собой!");
    }

    @Test
    void epicIntoYourself() {
        manager.getEpic(idNewEpic).addSubtaskId(idNewEpic);

        Assertions.assertNotNull(idNewEpic, "Отсутствует id созданного эпика");

        List<Integer> subtaskEpic = manager.getEpic(idNewEpic).getSubtaskId();
        Assertions.assertTrue(!subtaskEpic.contains(idNewEpic), "Эпик добавлен как подзадача самому себе!");

    }

    @Test
    void InMemoryTaskManagerAddTaskDifferentTypeAndFindThemById() {
        Assertions.assertNotNull(manager.getTask(idNewTask), "Задача не найдена!");
        Assertions.assertNotNull(manager.getEpic(idNewEpic), "Эпик не найден!");
        Assertions.assertNotNull(manager.getSubtask(idNewSubtask), "Подзадача не найдена!");
    }
}