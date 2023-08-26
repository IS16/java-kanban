package main.java.com.practikum.kanban;

import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.KVServer.KVServer;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Managers.TaskManager.HttpTaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.Tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, JsonParseException {
        new KVServer().start();

        HttpTaskManager taskManager = new HttpTaskManager("http://localhost:8078/", new Managers().getDefaultHistory());

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

        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getTaskById(task1.getId());

        HttpTaskManager taskManager1 = HttpTaskManager.loadFromDB("http://localhost:8078/");
        System.out.println(taskManager1.getPrioritizedTasks());
    }
}