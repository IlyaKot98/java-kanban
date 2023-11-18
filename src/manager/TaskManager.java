package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    ArrayList<Task> getTask();

    ArrayList<Epic> getEpic();

    ArrayList<Subtask> getSubtask();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void clearAllTask();

    void clearAllEpic();

    void clearAllSubtask();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    ArrayList<Subtask> getSubtasksEpic(int id);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
