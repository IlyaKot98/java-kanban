package tasks;

import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    protected ArrayList<Integer> subtaskId = new ArrayList<>();
    protected LocalDateTime endTime = LocalDateTime.now();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status, TaskType.EPIC, LocalDateTime.now(), Duration.ofMinutes(0));
    }

    public void addSubtaskId(int id) {
        if (id != this.getId()) {
            subtaskId.add(id);
        }
    }

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

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    public LocalDateTime getEndTime() {return endTime;}

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Epic epic = (Epic) object;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        final int total = 31;
        int result = 1;
        result = result * total + super.hashCode();
        result = result * total + ((subtaskId == null) ? 0 : subtaskId.hashCode());
        return result;
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
