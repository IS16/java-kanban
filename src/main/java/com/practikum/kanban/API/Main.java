package main.java.com.practikum.kanban.API;

import com.sun.net.httpserver.HttpServer;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Managers.TaskManager.FileBackedTasksManager;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.Tasks.TaskStatus;
import main.java.com.practikum.kanban.API.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Managers managers = new Managers();
        TaskManager taskManager = new FileBackedTasksManager(managers.getDefaultHistory(), "data.save");

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask("Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        Epic epic2 = new Epic("Второй эпик");
        taskManager.addEpic(epic2);

        Task task1 = new Task("Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);
        Task task2 = new Task("Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksList(taskManager));
        httpServer.createContext("/tasks/task", new Tasks(taskManager));
        httpServer.createContext("/tasks/subtask", new Subtasks(taskManager));
        httpServer.createContext("/tasks/epic", new Epics(taskManager));
        httpServer.createContext("/tasks/history", new History(taskManager));
        httpServer.start();

        System.out.printf("Server starts at port %d\n", PORT);
    }
}
