package manager;

import java.io.IOException;
import java.nio.file.Paths;

public class Managers {

    private Managers(){}
    public static TaskManager getDefault() {return new InMemoryTaskManager();}

    public static HistoryManager getDefaultHistory() {return new InMemoryHistoryManager();}

    public static FileBackedTaskManager getDefaultFile() throws IOException {return new FileBackedTaskManager(Paths.get("/Users/kotkov-il/Documents/test", "test.csv"));}
}
