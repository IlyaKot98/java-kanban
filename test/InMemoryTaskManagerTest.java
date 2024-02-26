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
    Epic epic = new Epic("Epic_1","epic.Epic 1 description", TaskStatus.NEW);
    Task task = new Task("TestTask_1", "Test_Task_1_Description", TaskStatus.NEW);
    Subtask subtask = new Subtask("Subtask_1","subtask.Subtask 1 description", epic.getId(), TaskStatus.NEW);


    @Test
    void addNewTask() {
        int idNewTask = manager.addNewTask(task);
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
        int idNewSubtask = manager.addNewSubtask(subtask);
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
        int idNewEpic = manager.addNewEpic(epic);
        final Epic savedEpic = manager.getEpic(idNewEpic);

        Assertions.assertNotNull(savedEpic, "Подзадача не найдена!");
        Assertions.assertEquals(epic, savedEpic, "Подзадачи не совпадают!");

        final List<Epic> epics = manager.getEpics();

        Assertions.assertNotNull(epic, "Подзадачи не загружены!");
        Assertions.assertEquals(1, epics.size(), "Неверное количество подзадач!");
        Assertions.assertEquals(subtask, epics.get(0), "Подзадачи не совпадают!");
    }

    @Test
    void removeTask() {
        int idNewTask = manager.addNewTask(task);
        final Task savedTask = manager.getTask(idNewTask);

        Assertions.assertNotNull(savedTask, "Задача не найдена!");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают!");

        final List<Task> tasksOld = manager.getTasks();
        manager.removeTask(idNewTask);
        final List<Task> tasksNew = manager.getTasks();

        Assertions.assertNotNull(tasksOld, "Список задач не загружен!");
        Assertions.assertNull(manager.getTask(idNewTask), "Задача не удалена!");
        Assertions.assertEquals(tasksOld.size() - 1, tasksNew, "Одинаковое количество задач в списке!");
    }

    @Test
    void removeEpic() {
        int idNewEpic = manager.addNewEpic(epic);
        final Epic savedEpic = manager.getEpic(idNewEpic);

        Assertions.assertNotNull(savedEpic, "Эпик не найден!");
        Assertions.assertEquals(epic, savedEpic, "Эпики не совпадают!");

        final List<Epic> epicsOld = manager.getEpics();
        manager.removeEpic(idNewEpic);
        final List<Epic> epicsNew = manager.getEpics();

        Assertions.assertNotNull(epicsOld, "Список эпикв не загружен!");
        Assertions.assertNull(manager.getEpic(idNewEpic), "Эпик не удален!");
        Assertions.assertEquals(epicsOld.size() - 1, epicsNew, "Одинаковое количество эпиков в списке!");
    }

    @Test
    void removeSubtask() {
        int idNewSubtask = manager.addNewSubtask(subtask);
        final Subtask savedSubtask = manager.getSubtask(idNewSubtask);

        Assertions.assertNotNull(savedSubtask, "Эпик не найден!");
        Assertions.assertEquals(subtask, savedSubtask, "Эпики не совпадают!");

        final List<Subtask> subtasksOld = manager.getSubtasks();
        manager.removeEpic(idNewSubtask);
        final List<Subtask> subtasksNew = manager.getSubtasks();

        Assertions.assertNotNull(subtasksOld, "Список эпикв не загружен!");
        Assertions.assertNull(manager.getEpic(idNewSubtask), "Эпик не удален!");
        Assertions.assertEquals(subtasksOld.size() - 1, subtasksNew, "Одинаковое количество эпиков в списке!");
    }
}