package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected Path path;
    protected File file;

    public FileBackedTaskManager(Path path) throws IOException {
        this.path = path;
    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws IOException {
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        file = path.toFile();
        try {
            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false)) {
                fileWriter.write("id,type,name,status,description,epic\n");
                //fileWriter.write(historyToString());
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении!");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void loadFromFile() throws IOException {
        String str = Files.readString(Paths.get("/Users/kotkov-il/Documents/test", "test.csv"));

        //List<Task> test = historyFromString(str);



/*
        List<Task> history = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(str.split("\n")));
        lines.remove(0);
        for (String line : lines) {
            historyManager.addTask(fromString(line));
        }*/
    }

    public String toString(Task task) {
        StringBuilder sb = new StringBuilder(task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription());
        if (task.getTaskType() == TaskType.SUBTASK) {
            sb.append("," + ((Subtask) task).getEpicId());
        } else {
            sb.append("," + task.getTaskType());
        }
        return sb.toString();
    }

    public Task fromString(String value) {
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

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                   task.getDescription() + ",");
            if (task.getTaskType() == TaskType.SUBTASK) {
                sb.append(((Subtask) task).getEpicId() + "\n");
            } else {
                sb.append("," + task.getTaskType());
            }
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(value.split("\n")));
        lines.remove(0);
        for (String line : lines) {
            String[] word = line.split(",");
            history.add(Integer.valueOf(word[0]));
        }
        return history;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        String str = Files.readString(file.toPath());




        List<Task> history = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(str.split("\n")));
        lines.remove(0);
        for (String line : lines) {
            fromString(line);
        }
        return ;
    }

    @Override
    public Task getTask(int id) throws IOException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) throws IOException {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) throws IOException {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeTask(int id) throws IOException {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) throws IOException {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) throws IOException {
        super.removeSubtask(id);
        save();
    }
}
