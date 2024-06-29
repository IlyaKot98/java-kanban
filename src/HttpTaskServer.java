import com.sun.net.httpserver.HttpServer;
import handlers.TaskHandler;
import handlers.UserHandler;
import manager.FileBackedTaskManager;
import tasks.Epic;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class HttpTaskServer {

    FileBackedTaskManager manager;
    private static final int PORT = 8080;

    public HttpTaskServer (FileBackedTaskManager manager) {
        this.manager = manager;
    }

    public static void main(String[] args) throws IOException {

        File file = new File( "test.csv");
        if(!Files.exists(file.toPath())){
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        HttpTaskServer httpTaskServer = new HttpTaskServer(FileBackedTaskManager.loadFromFile(file));

        httpTaskServer.start();
    }

    void start () throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);

        TaskHandler taskHandler = new TaskHandler(manager);
        UserHandler userHandler = new UserHandler(manager);

        httpServer.createContext("/tasks", taskHandler);
        httpServer.createContext("/subtasks", taskHandler);
        httpServer.createContext("/epics", taskHandler);
        httpServer.createContext("/history", userHandler);
        httpServer.createContext("/prioritized", userHandler);
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }
}

