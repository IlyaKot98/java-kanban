package tasks;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected String status;
    public Task(String name, String description, String status){
        this.name = name;
        this.description = description;
        this.status = "NEW";
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '}';
    }
}
