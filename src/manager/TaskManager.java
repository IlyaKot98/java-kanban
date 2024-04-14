package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    Task getTask(int id) throws IOException;

    Epic getEpic(int id) throws IOException;

    Subtask getSubtask(int id) throws IOException;

    void clearAllTask();

    void clearAllEpic() throws IOException;

    void clearAllSubtask() throws IOException;

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTask(int id) throws IOException;

    void removeEpic(int id) throws IOException;

    void removeSubtask(int id) throws IOException;

    List<Subtask> getSubtasksEpic(int id);

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();
}
