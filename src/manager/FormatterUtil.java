package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FormatterUtil {

    private FormatterUtil() {}

    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + ",");
        if (task.getTaskType() == TaskType.SUBTASK) {
            sb.append(((Subtask) task).getEpicId());
        } else {
            sb.append(task.getTaskType());
        }
        sb.append("\n");

        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] values = value.split(",");
        Task task;
        if(TaskType.SUBTASK.toString().equals(values[1])) {
            task = new Subtask(values[2], values[4], Integer.parseInt(values[5]), TaskStatus.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        } else if(TaskType.EPIC.toString().equals(values[1])) {
            task = new Epic(values[2], values[4], TaskStatus.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        } else {
            task = new Task(values[2], values[4], TaskStatus.valueOf(values[3]));
            task.setId(Integer.parseInt(values[0]));
        }
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(toString(task));
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(value.split("\n")));
        lines.remove(0);
        for (String line : lines) {
            String[] word = line.split(",");
            history.add(Integer.parseInt(word[0]));
        }
        return history;
    }
}
