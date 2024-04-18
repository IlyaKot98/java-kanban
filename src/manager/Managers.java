package manager;

import java.io.IOException;
import java.nio.file.Paths;

public class Managers {

    private Managers(){}
    public static TaskManager getDefault() {return new InMemoryTaskManager();}

    public static HistoryManager getDefaultHistory() {return new InMemoryHistoryManager();}
}
