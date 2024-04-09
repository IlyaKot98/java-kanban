package tasks;

import java.util.Objects;

public class Task{
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected TaskType taskType;
    public Task(String name, String description, TaskStatus status, TaskType taskType){
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = taskType;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        taskType = TaskType.TASK;
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

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                Objects.equals(status, task.status);
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '}';
    }
}
