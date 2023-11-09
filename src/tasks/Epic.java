package tasks;

import tasks.Task;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int id){subtaskId.add(id);}

    public void cleanSubtaskId(){subtaskId.clear();}

    public void removeSubtaskId(int id) {
        for(int i = 0; i < subtaskId.size(); i++){
            if (subtaskId.get(i) == id){
                subtaskId.remove(i);
            }
        }
    }

    public ArrayList<Integer> getSubtaskId(){
        return subtaskId;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", description=" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtask=" + subtaskId.toString() + "}";
    }
}
