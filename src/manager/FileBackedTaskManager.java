package manager;

import tasks.Task;
import tasks.TaskType;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected Files path;

    public FileBackedTaskManager(Files path) {
        this.path = path;
    }

    /*
    id,type,name,status,description,epic
    1,TASK,Task1,NEW,Description task1,
    2,EPIC,Epic2,DONE,Description epic2,
    3,SUBTASK,Sub Task2,DONE,Description sub task3,2
     */

    public void save() throws IOException {
        try (FileWriter fileWriter = new FileWriter("test.txt")) {
            List<Task> history = getHistory();
            for (int i = 0; i < history.size(); i++) {
                fileWriter.write(toString(history.get(i)) + "\n");
            }
        }
    }

    public String toString(Task task) {

        StringBuilder sb = new StringBuilder(task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription());
        //sb.append(ge);

        //sb.close();
        return "test";
    }

   /* public Task fromString(String taskString) {


    }*/

}
