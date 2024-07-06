package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task{
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType taskType;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, TaskStatus status, TaskType taskType, LocalDateTime startTime, Duration duration){
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, TaskType taskType, LocalDateTime startTime){
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
        this.startTime = startTime;
        //this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        taskType = TaskType.TASK;
        this.duration = duration;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus(){
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {return description;}

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public LocalDateTime getStartTime() {return startTime;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public Duration getDuration() {return duration;}

    public void setDuration(Duration duration) {this.duration = duration;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status) &&
                Objects.equals(taskType, task.taskType);
    }

    @Override
    public int hashCode() {
        final int total = 31;
        int result = 1;
        result = result * total + id;
        result = result * total + ((name == null) ? 0 : name.hashCode());
        result = result * total + ((description == null) ? 0 : description.hashCode());
        result = result * total + ((status == null) ? 0 : status.hashCode());
        result = result * total + ((taskType == null) ? 0 : taskType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '\'' +
                ", startTime=" + startTime + '\'' +
                ", duration=" + duration + '}';
    }
}
