package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap <Integer, Epic> epics = new HashMap<>();
    private final HashMap <Integer, Subtask> subtasks = new HashMap();
    private final HashMap <Integer, Task> tasks = new HashMap();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int generatorId = 1;
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
       if (task != null) historyManager.addTask(task);
       return task;
   }

   @Override
   public Epic getEpic(int id) {
       Epic epic = epics.get(id);
       if (epic != null) historyManager.addTask(epic);
       return epic;
   }

   @Override
   public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) historyManager.addTask(subtask);
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
   public int addNewTask(Task task) {
       boolean taskIntersects = getPrioritizedTasks().stream().anyMatch(taskInStream -> ifTasksIntersects(taskInStream, task));
       try {
           if (taskIntersects) {
               throw new CreateException("Задача пересекается по времени с другими!");
           } else {
               final int id = intersectsIdTasks();
               if ((task.getId() == 0) || false) task.setId(id);
               tasks.put(task.getId(), task);
               updatePrioritizedTasks(task);
               return task.getId();
           }
       } catch (CreateException e) {
           return 0;
       }
   }

   @Override
   public int addNewEpic(Epic epic){
       final int id = intersectsIdTasks();
       if (epic.getId() == 0 || false) epic.setId(id);
       epics.put(epic.getId(), epic);
       actualSubtaskInEpic(epic);
       return epic.getId();
   }

   @Override
   public int addNewSubtask(Subtask subtask){
       boolean taskIntersects = getPrioritizedTasks().stream().anyMatch(taskInStream -> ifTasksIntersects(taskInStream, subtask));
       try {
           if (taskIntersects) {
               throw new CreateException("Задача пересекается по времени с другими!");
           } else {
               final int id = intersectsIdTasks();
               if (subtask.getId() == 0 || false) subtask.setId(id);

               subtasks.put(subtask.getId(), subtask);
               try {
                   if (epics.get(subtask.getEpicId()) == null) {
                       throw new CreateException("Не найден эпик для подзадачи!");
                   } else {
                       Epic epic = epics.get(subtask.getEpicId());
                       epic.addSubtaskId(id);
                       updateEpicStatus(epic);
                       updateEpicTimeStart(epic);
                       updateEpicDuration(epic, subtask);
                       updateEpicEndTime(epic);
                   }
               } catch (CreateException e) {
                   System.out.println(e.getMessage() + " " + subtask);
               }
               updatePrioritizedTasks(subtask);
               return subtask.getId();
           }
       } catch (CreateException e) {
           return 0;
       }
   }

   @Override
   public void updateTask(Task task) throws CreateException {
       boolean taskIntersects = getPrioritizedTasks().stream()
               .filter(taskInStream -> taskInStream.getId() != task.getId())
               .anyMatch(taskInStream -> ifTasksIntersects(taskInStream, task));
       if (taskIntersects) {
           throw new CreateException();
       } else {
           tasks.put(task.getId(), task);
           updatePrioritizedTasks(task);
       }
   }

   @Override
   public void updateEpic(Epic epic) {
       epics.put(epic.getId(), epic);
   }

   @Override
   public void updateSubtask(Subtask subtask) throws CreateException {
       boolean subtaskIntersects = getPrioritizedTasks().stream()
               .filter(subtaskInStream -> subtaskInStream.getId() != subtask.getId())
               .anyMatch(subtaskInStream -> ifTasksIntersects(subtaskInStream, subtask));
       if (subtaskIntersects) {
           throw new CreateException();
       } else {
           subtasks.put(subtask.getId(), subtask);
           updateEpicStatus(epics.get(subtask.getEpicId()));
       }
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

   private int intersectsIdTasks() {
       int id = generatorId;
       while (tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id)) {
           id = ++generatorId;
       }
       return id;
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

   private void updateEpicTimeStart(Epic epic) {
       Set<LocalDateTime> subtaskStartTime = epic.getSubtaskId().stream()
               .map(startTime -> subtasks.get(startTime).getStartTime()).collect(Collectors.toSet());

       for (LocalDateTime localDateTime : subtaskStartTime) {
           if (localDateTime.isBefore(epic.getStartTime())) {
               epic.setStartTime(localDateTime);
           }
       }
   }

   private void updateEpicDuration(Epic epic, Subtask subtask) {
       epic.setDuration(epic.getDuration().plus(subtask.getDuration()));
   }

   private void updateEpicEndTime(Epic epic) {
       for (Integer subtaskId : epic.getSubtaskId()) {
           if (epic.getEndTime().isBefore(subtasks.get(subtaskId).getEndTime())) epic.setEndTime(subtasks.get(subtaskId).getEndTime());
       }
   }

   private void updatePrioritizedTasks(Task task) {
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

   private void actualSubtaskInEpic(Epic epic) {
       for (Subtask subtask : subtasks.values()) {
           if (subtask.getEpicId() == epic.getId()) {
               if (!epic.getSubtaskId().contains(subtask.getId())) epic.addSubtaskId(subtask.getId());

               updateEpicStatus(epic);
               updateEpicTimeStart(epic);
               updateEpicDuration(epic, subtask);
               updateEpicEndTime(epic);
           }
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
