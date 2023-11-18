package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    List<Task> history = new ArrayList<>();

    public List<Task> getHistory(){
        return history;
    }

    public void addTask(Task task){
        if(history.size() >= 10){
            history.remove(0);
            history.add(task);
        } else {
            history.add(task);
        }
    }
}
