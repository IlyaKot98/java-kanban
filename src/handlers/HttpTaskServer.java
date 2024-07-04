package handlers;

import com.sun.net.httpserver.HttpServer;
import manager.FileBackedTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    FileBackedTaskManager manager;
    private static final int PORT = 8080;
    HttpServer httpServer = HttpServer.create();

    public HttpTaskServer (FileBackedTaskManager manager) throws IOException {
        this.manager = manager;
    }

    public static void main(String[] args) {

    }

    void start () throws IOException {
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

    void stop() {
        httpServer.stop(10);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }
}

