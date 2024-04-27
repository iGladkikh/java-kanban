package api;

import com.sun.net.httpserver.HttpServer;
import services.FileBackedTaskManager;
import services.Managers;
import services.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

import static java.lang.System.getProperty;

public class HttpTaskServer {
    private static final int DEFAULT_PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer() {
        this(DEFAULT_PORT, Managers.getDefaultTaskManager());
    }

    public HttpTaskServer(TaskManager taskManager) {
        this(DEFAULT_PORT, taskManager);
    }

    public HttpTaskServer(int port, TaskManager taskManager) {
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/" + TaskHandler.PATH, new TaskHandler(taskManager));
            httpServer.createContext("/" + EpicHandler.PATH, new EpicHandler(taskManager));
            httpServer.createContext("/" + SubtaskHandler.PATH, new SubtaskHandler(taskManager));
            httpServer.createContext("/" + HistoryHandler.PATH, new HistoryHandler(taskManager));
            httpServer.createContext("/" + PrioritizedHandler.PATH, new PrioritizedHandler(taskManager));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка запуска HTTP-сервера на " + port + " порту.");
        }
    }

    public static void main(String[] args) {
        String dataFileDirectory = Paths.get(getProperty("user.dir"), "data").toString();
        String testManagerDataFile = "testManagerData.csv";
        File dataFile = Paths.get(dataFileDirectory, testManagerDataFile).toFile();
        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(dataFile);

        HttpTaskServer taskServer = new HttpTaskServer(fileBackedTaskManager);
        taskServer.start();
    }

    public void start() {
        if (httpServer == null) {
            return;
        }
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + httpServer.getAddress().getPort() + " порту.");
    }

    public void stop() {
        if (httpServer == null) {
            return;
        }
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен.");
    }
}
