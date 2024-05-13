package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try {
            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false)) {
                String str = FormatterUtil.historyToString(historyManager);
                if (!str.isEmpty()) {
                    fileWriter.write("id,type,name,status,description,epic\n");
                    fileWriter.write(str);
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка при сохранении!");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        String str = Files.readString(file.toPath());
        FileBackedTaskManager fbtm = new FileBackedTaskManager(file);
        List<Integer> history = FormatterUtil.historyFromString(str);

        List<String> lines = new ArrayList<>(List.of(str.split("\n")));
        lines.remove(0);
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            tasks.add(FormatterUtil.fromString(line));
        }

        Comparator<Task> comparator = new TaskTypeComparator();
        tasks.sort(comparator);

        for (Task task : tasks) {
            int idNew;
            if (task.getTaskType().toString().equals(TaskType.TASK.toString())) {
                idNew = task.getId();
                fbtm.addNewTask(task);
                task.setId(idNew);
            } else if (task.getTaskType().toString().equals(TaskType.EPIC.toString())) {
                idNew = task.getId();
                fbtm.addNewEpic((Epic) task);
                task.setId(idNew);
            } else {
                idNew = task.getId();
                fbtm.addNewSubtask((Subtask) task);
                task.setId(idNew);
            }
        }

        for (int id : history) {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                if(task.getId() == id) {
                    fbtm.historyManager.addTask(task);
                }
            }
        }

        return fbtm;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }
}

class TaskTypeComparator implements Comparator<Task> {

    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getId() > t2.getId()) {
            return 1;
        } else if (t1.getId() < t2.getId()) {
            return -1;
        } else {
            return 0;
        }
    }

}
