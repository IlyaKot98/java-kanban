package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    void addTask(Task task);
    void remove(int id);
    ArrayList<Task> getHistory();
}
