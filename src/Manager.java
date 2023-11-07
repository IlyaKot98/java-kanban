import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class Manager {

    HashMap <Integer, Epic> epics = new HashMap<>();
    HashMap <Integer, Subtask> subtasks = new HashMap();
    HashMap <Integer, Task> tasks = new HashMap();
    int generatorId = 0;


   public ArrayList<Task> getListAllTask(){
       ArrayList<Task> listAllTask = new ArrayList<>(tasks.values());
       return listAllTask;
   }

   public ArrayList<Epic> getListAllEpic(){
       ArrayList<Epic> listAllEpic = new ArrayList<>(epics.values());
       return listAllEpic;
   }

   public ArrayList<Subtask> getListAllSubtask(){
       ArrayList<Subtask> listAllSubtask = new ArrayList<>(subtasks.values());
       return listAllSubtask;
   }

   public Task getTask(int id){
        return tasks.get(id);
   }

   public Epic getEpic(int id){
       return epics.get(id);
   }

    public Subtask getSubtask(int id){
       return subtasks.get(id);
    }

   public void clearAllTask(){
       tasks.clear();
   }

   public void clearAllEpic(){
       for (Epic epic : epics.values()){
           ArrayList <Integer> subtuskId = epic.subtaskId;
           for(int i = 0; i < subtuskId.size(); i++) {
               subtasks.remove(subtuskId.get(i));
           }
       }
       epics.clear();
   }

   public void clearAllSubtask(){
       for (Subtask subtask : subtasks.values()){
           Epic epic = getEpic(subtask.getEpicId());
           epic.cleanSubtaskId();
       }
       subtasks.clear();
   }

   public int addNewTask(Task task){
       final int id = ++generatorId;
       task.setId(id);
       tasks.put(id, task);
       return id;
   }

   public int addNewEpic(Epic epic){
       final int id = ++generatorId;
       epic.setId(id);
       epics.put(id, epic);
       return id;
   }

   public int addNewSubtask(Subtask subtask){
       final int id = ++generatorId;
       subtask.setId(id);
       subtasks.put(id, subtask);
       Epic epic = getEpic(subtask.getEpicId());
       epic.addSubtaskId(id);
       return id;
   }

   public void updateTask(Task task){
       tasks.put(task.getId(), task);
   }

   public void updateEpic(Epic epic){
       epics.put(epic.getId(), epic);
   }

   public void updateSubtask(Subtask subtask){
       subtasks.put(subtask.getId(), subtask);
   }

   public void removeTask(int id){
       tasks.remove(id);
   }

   public void removeEpic(int id){
       ArrayList<Integer> subtaskId = epics.get(id).subtaskId;
       for(int i = 0; i < subtaskId.size(); i++){
           subtasks.remove(subtaskId.get(i));
       }
       epics.remove(id);
   }

   public void removeSubtask(int id){
       Subtask subtask = subtasks.get(id);
       Epic epic = getEpic(subtask.getEpicId());
       epic.removeSubtaskId(id);
       subtasks.remove(id);
   }

   public ArrayList<Subtask> getSubtasksEpic(int id){
       ArrayList<Subtask> subtasksEpic = new ArrayList<>();
       for (Integer i : epics.get(id).subtaskId){
           subtasksEpic.add(subtasks.get(i));
       }
       return subtasksEpic;
   }

    public void test(){

       System.out.println("Done");
   }
}
