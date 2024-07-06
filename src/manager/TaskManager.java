package manager;

import exception.CreateException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void clearAllTask();

    void clearAllEpic();

    void clearAllSubtask();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    void updateTask(Task task) throws CreateException;

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask) throws CreateException;

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    List<Subtask> getSubtasksEpic(int id);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
