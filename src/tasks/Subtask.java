package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task{
    protected int epicId;

    public Subtask(String name, String description, int epicId, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, TaskType.SUBTASK, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId(){return epicId;}

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Subtask subtask = (Subtask) object;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        final int total = 31;
        int result = 1;
        result = result * total + super.hashCode();
        result = result * total + epicId;
        return result;
    }

    @Override
    public String toString(){
        return "tasks.Subtask{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + '}';
    }
}
