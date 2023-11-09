import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class Manager {

    private HashMap <Integer, Epic> epics = new HashMap<>();
    private HashMap <Integer, Subtask> subtasks = new HashMap();
    private HashMap <Integer, Task> tasks = new HashMap();
    private int generatorId = 0;

   public ArrayList<Task> getListAllTask(){
       return new ArrayList<>(tasks.values());
   }

   public ArrayList<Epic> getListAllEpic(){
       return new ArrayList<>(epics.values());
   }

   public ArrayList<Subtask> getListAllSubtask(){
       return new ArrayList<>(subtasks.values());
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
       subtasks.clear();
       epics.clear();
   }

   public void clearAllSubtask(){
       for (Subtask subtask : subtasks.values()){
           Epic epic = getEpic(subtask.getEpicId());
           epic.cleanSubtaskId();
           updateEpicStatus(epic);
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
       updateEpicStatus(epic);
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
       updateEpicStatus(epics.get(subtask.getEpicId()));
   }

   public void removeTask(int id){
       tasks.remove(id);
   }

   public void removeEpic(int id){
       ArrayList<Integer> subtaskId = epics.get(id).getSubtaskId();
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
       updateEpicStatus(epic);
   }

   public ArrayList<Subtask> getSubtasksEpic(int id){
       ArrayList<Subtask> subtasksEpic = new ArrayList<>();
       for (Integer i : epics.get(id).getSubtaskId()){
           subtasksEpic.add(subtasks.get(i));
       }
       return subtasksEpic;
   }

   public void updateEpicStatus(Epic epic){
       Set<String> subtasksEpic = new HashSet<>();
       for(Integer i : epic.getSubtaskId()){
           subtasksEpic.add(subtasks.get(i).getStatus());
       }
       if (subtasksEpic == null || subtasksEpic.isEmpty()
               || subtasksEpic.contains("NEW")
               && !subtasksEpic.contains("IN_PROGRESS")
               && !subtasksEpic.contains("DONE")){
           epic.setStatus("NEW");
       } else if (!subtasksEpic.contains("NEW") && !subtasksEpic.contains("IN_PROGRESS")
               && subtasksEpic.contains("DONE")) {
           epic.setStatus("DONE");
       } else {
           epic.setStatus("IN_PROGRESS");
       }
   }
}
