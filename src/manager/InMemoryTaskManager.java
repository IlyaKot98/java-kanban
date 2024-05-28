package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap <Integer, Epic> epics = new HashMap<>();
    private final HashMap <Integer, Subtask> subtasks = new HashMap();
    private final HashMap <Integer, Task> tasks = new HashMap();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
           prioritizedTasks.remove(subtask);
       }
       subtasks.clear();
   }

   @Override
   public int addNewTask(Task task){
       boolean taskIntersects = getPrioritizedTasks().stream().anyMatch(taskInStream -> ifTasksIntersects(taskInStream, task));
       try {
           if (taskIntersects) {
               throw new CreateException("Задача пересекается по времени с другими!");
           } else {
               final int id = ++generatorId;
               task.setId(id);
               tasks.put(id, task);
               updatePrioritizedTasks(task);
               return id;
           }
       } catch (CreateException e) {
           System.out.println(e.getMessage() + " " + task);
       }
       return 0;
   }

   @Override
   public int addNewEpic(Epic epic){
       final int id = ++generatorId;
       epics.put(id, epic);
       epic.setId(id);
       return id;
   }

   @Override
   public int addNewSubtask(Subtask subtask){
       boolean taskIntersects = getPrioritizedTasks().stream().anyMatch(taskInStream -> ifTasksIntersects(taskInStream, subtask));
       try {
           if (taskIntersects) {
               throw new CreateException("Задача пересекается по времени с другими!");
           } else {
               final int id = ++generatorId;
               subtask.setId(id);
               subtasks.put(id, subtask);
               Epic epic = epics.get(subtask.getEpicId());
               epic.addSubtaskId(id);
               updateEpicStatus(epic);
               updateEpicTimeStart(epic);
               updateEpicDuration(epic, subtask);
               updateEpicEndTime(epic);
               updatePrioritizedTasks(subtask);
               return id;
           }
       } catch (CreateException e) {
           System.out.println(e.getMessage() + " " + subtask);
       }
       return 0;
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
       prioritizedTasks.remove(tasks.get(id));
       tasks.remove(id);
       historyManager.remove(id);
   }

   @Override
   public void removeEpic(int id) {
       prioritizedTasks.remove(epics.get(id));
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
       prioritizedTasks.remove(subtasks.get(id));
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
               || subtasksEpic.contains(TaskStatus.NEW)
               && !subtasksEpic.contains(TaskStatus.IN_PROGRESS)
               && !subtasksEpic.contains(TaskStatus.DONE)){
           epic.setStatus(TaskStatus.NEW);
       } else if (!subtasksEpic.contains(TaskStatus.NEW) && !subtasksEpic.contains(TaskStatus.IN_PROGRESS)
               && subtasksEpic.contains(TaskStatus.DONE)) {
           epic.setStatus(TaskStatus.DONE);
       } else if (subtasksEpic.contains(TaskStatus.IN_PROGRESS)) {
           epic.setStatus(TaskStatus.IN_PROGRESS);
       }
   }

   public void updateEpicTimeStart(Epic epic) {
       Set<LocalDateTime> subtaskStartTime = epic.getSubtaskId().stream()
               .map(startTime -> subtasks.get(startTime).getStartTime()).collect(Collectors.toSet());

       for (LocalDateTime localDateTime : subtaskStartTime) {
           if (localDateTime.isBefore(epic.getStartTime())) {
               epic.setStartTime(localDateTime);
           }
       }
   }

   public void updateEpicDuration(Epic epic, Subtask subtask) {
       epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
   }

   public void updateEpicEndTime(Epic epic) {
       for (Integer subtaskId : epic.getSubtaskId()) {
           if (epic.getEndTime().isBefore(subtasks.get(subtaskId).getEndTime())) epic.setEndTime(subtasks.get(subtaskId).getEndTime());
       }
   }

   public void updatePrioritizedTasks(Task task) {
       if (tasks.containsValue(task) || epics.containsValue(task) || subtasks.containsValue(task)) {
           if (task.getStartTime() != null) prioritizedTasks.add(task);
       } else {
           prioritizedTasks.remove(task);
       }
   }

   private boolean ifTasksIntersects(Task taskInStream, Task task) {
       if (task.getStartTime().isBefore(taskInStream.getStartTime())
               && task.getEndTime().isBefore(taskInStream.getStartTime())) {
           return false;
       } else if (task.getStartTime().isAfter(taskInStream.getEndTime())) {
           return false;
       } else {
           return true;
       }
   }

   public Set<Task> getPrioritizedTasks() {
       return prioritizedTasks;
   }

   @Override
   public List<Task> getHistory(){
       return historyManager.getHistory();
   }
}
