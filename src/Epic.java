import java.util.ArrayList;

public class Epic extends Task{

    ArrayList<Integer> subtaskId = new ArrayList<>();

    Epic(String name, String description, String status) {
        super(name, description, status);
    }

    public void addSubtaskId(int id){subtaskId.add(id);}

    public void cleanSubtaskId(){subtaskId.clear();}

    public void removeSubtaskId(int id){
        for(int i = 0; i < subtaskId.size(); i++){
            if (subtaskId.get(i) == id){
                subtaskId.remove(i);
            }
        }
    }

    @Override
    public String toString(){
        return "Epic{" +
                "name='" + name + '\'' +
                ", description=" + description + '\'' +
                ", status=" + status + '\'' +
                ", subtask=" + subtaskId.toString() + "}";
    }
}
