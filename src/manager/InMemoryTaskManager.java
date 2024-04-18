package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap <Integer, Epic> epics = new HashMap<>();
    private final HashMap <Integer, Subtask> subtasks = new HashMap();
    private final HashMap <Integer, Task> tasks = new HashMap();
    private int generatorId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

   @Override
   public List<Task> getTasks(){
       return new ArrayList<>(tasks.values());
   }

   @Override
   public List<Epic> getEpics(){
       return new ArrayList<>(epics.values());
   }

   @Override
   public List<Subtask> getSubtasks(){
       return new ArrayList<>(subtasks.values());
   }

   @Override
   public Task getTask(int id) {
       Task task = tasks.get(id);
       historyManager.addTask(task);
        return task;
   }

   @Override
   public Epic getEpic(int id) {
       Epic epic = epics.get(id);
       historyManager.addTask(epic);
       return epic;
   }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTask(subtask);
       return subtask;
    }

   @Override
   public void clearAllTask(){
       tasks.clear();
   }

   @Override
   public void clearAllEpic() {
       clearAllSubtask();
       epics.clear();
   }

   @Override
   public void clearAllSubtask() {
       for (Subtask subtask : subtasks.values()){
           Epic epic = getEpic(subtask.getEpicId());
           epic.cleanSubtaskId();
           updateEpicStatus(epic);
       }
       subtasks.clear();
   }

   @Override
   public int addNewTask(Task task){
       final int id = ++generatorId;
       task.setId(id);
       tasks.put(id, task);
       return id;
   }

   @Override
   public int addNewEpic(Epic epic){
       final int id = ++generatorId;
       epic.setId(id);
       epics.put(id, epic);
       return id;
   }

   @Override
   public int addNewSubtask(Subtask subtask){
       final int id = ++generatorId;
       subtask.setId(id);
       subtasks.put(id, subtask);
       Epic epic = epics.get(subtask.getEpicId());
       epic.addSubtaskId(id);
       updateEpicStatus(epic);
       return id;
   }

   @Override
   public void updateTask(Task task){
       tasks.put(task.getId(), task);
   }

   @Override
   public void updateEpic(Epic epic){
       epics.put(epic.getId(), epic);
   }

   @Override
   public void updateSubtask(Subtask subtask){
       subtasks.put(subtask.getId(), subtask);
       updateEpicStatus(epics.get(subtask.getEpicId()));
   }

   @Override
   public void removeTask(int id) {
       tasks.remove(id);
       historyManager.remove(id);
   }

   @Override
   public void removeEpic(int id) {
       final Epic epic = epics.remove(id);
       if (epic == null) {
           return;
       }
       historyManager.remove(id);
       for (Integer subtaskId : epic.getSubtaskId()) {
           subtasks.remove(subtaskId);
           historyManager.remove(subtaskId);
       }
   }

   @Override
   public void removeSubtask(int id) {
       Subtask subtask = subtasks.get(id);
       Epic epic = getEpic(subtask.getEpicId());
       epic.removeSubtaskId(id);
       subtasks.remove(id);
       updateEpicStatus(epic);
       historyManager.remove(id);
   }

   @Override
   public List<Subtask> getSubtasksEpic(int id){
       ArrayList<Subtask> subtasksEpic = new ArrayList<>();
       for (Integer i : epics.get(id).getSubtaskId()){
           subtasksEpic.add(subtasks.get(i));
       }
       return subtasksEpic;
   }

   @Override
   public void updateEpicStatus(Epic epic){
       Set<TaskStatus> subtasksEpic = new HashSet<>();
       for(Integer i : epic.getSubtaskId()){
           subtasksEpic.add(subtasks.get(i).getStatus());
       }
       if (subtasksEpic == null || subtasksEpic.isEmpty()
               || subtasksEpic.contains("NEW")
               && !subtasksEpic.contains("IN_PROGRESS")
               && !subtasksEpic.contains("DONE")){
           epic.setStatus(TaskStatus.NEW);
       } else if (!subtasksEpic.contains("NEW") && !subtasksEpic.contains("IN_PROGRESS")
               && subtasksEpic.contains("DONE")) {
           epic.setStatus(TaskStatus.DONE);
       } else {
           epic.setStatus(TaskStatus.IN_PROGRESS);
       }
   }

   @Override
   public List<Task> getHistory(){
       return historyManager.getHistory();
   }
}
