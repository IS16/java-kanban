package com.practikum.kanban.Managers.TaskManager;

import com.practikum.kanban.Exceptions.ManagerSaveException;
import com.practikum.kanban.Managers.HistoryManager.HistoryManager;
import com.practikum.kanban.Managers.Managers;
import com.practikum.kanban.Tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final String filename;
    public FileBackedTasksManager(HistoryManager historyManager, String filename) {
        super(historyManager);
        this.filename = filename;
    }

    public static FileBackedTasksManager loadFromFile(String filename) {
        ArrayList<String> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (br.ready()) {
                data.add(br.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при открытии файла!");
        }

        Managers managers = new Managers();
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(managers.getDefaultHistory(), filename);
        if (data.size() == 0) {
            return fileBackedTasksManager;
        }

        boolean isHistory = false;
        HashMap<Integer, Task> entities = new HashMap<>();
        int maxId = 0;

        for (String line : data.subList(1, data.size())) {
            if (line.isBlank()) {
                isHistory = true;
                continue;
            }

            if (!isHistory) {
                String[] parts = line.split(",");

                if (TaskType.TASK == TaskType.valueOf(parts[1])) {
                    Task newEnitity = Task.fromString(line);
                    maxId = Math.max(newEnitity.getId(), maxId);
                    entities.put(newEnitity.getId(), newEnitity);
                    fileBackedTasksManager.addTask(newEnitity);
                } else if (TaskType.EPIC == TaskType.valueOf(parts[1])) {
                    Epic newEnitity = Epic.fromString(line);
                    maxId = Math.max(newEnitity.getId(), maxId);
                    entities.put(newEnitity.getId(), newEnitity);
                    fileBackedTasksManager.addEpic(newEnitity);
                } else if (TaskType.SUBTASK == TaskType.valueOf(parts[1])) {
                    Subtask newEnitity = Subtask.fromString(line);
                    maxId = Math.max(newEnitity.getId(), maxId);
                    entities.put(newEnitity.getId(), newEnitity);
                    fileBackedTasksManager.addSubtask(newEnitity.getEpicId(), newEnitity);
                }
            } else {
                ArrayList<Integer> ids = FileBackedTasksManager.historyFromString(line);

                for (Integer id : ids) {
                    fileBackedTasksManager.historyManager.add(entities.get(id));
                }
            }
        }

        fileBackedTasksManager.curId = maxId;
        return fileBackedTasksManager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task result = super.getTaskById(taskId);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic result = super.getEpicById(epicId);
        save();
        return result;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        super.addSubtask(epicId, subtask);
        save();
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask result = super.getSubtaskById(subtaskId);
        save();
        return result;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteAllSubtasksByEpicId(int epicId) {
        super.deleteAllSubtasksByEpicId(epicId);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : super.getAllTasks()) {
                bw.write(task.toString() + "\n");
            }

            for (Epic epic : super.getAllEpics()) {
                bw.write(epic.toString() + "\n");
            }

            for (Subtask subtask : super.getAllSubtasks()) {
                bw.write(subtask.toString() + "\n");
            }

            bw.write("\n");
            bw.write(FileBackedTasksManager.historyToString(super.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка при сохранении!");
        }
    }

    private static String historyToString(HistoryManager historyManager) {
        ArrayList<String> out = new ArrayList<>();

        for (Task task : historyManager.getHistory()) {
            out.add(String.valueOf(task.getId()));
        }
        return String.join(",", out);
    }

    private static ArrayList<Integer> historyFromString(String value) {
        ArrayList<Integer> out = new ArrayList<>();

        String[] parts = value.split(",");
        for (String item : parts) {
            out.add(Integer.parseInt(item));
        }

        return out;
    }

    public static void main(String[] args) {
        Managers managers = new Managers();
        FileBackedTasksManager taskManager = new FileBackedTasksManager(managers.getDefaultHistory(), "data.save");

        Task task1 = new Task(taskManager.getCurId(), "Первая", "Моя первая задача", TaskStatus.NEW);
        Task task2 = new Task(taskManager.getCurId(), "Вторая", "Моя вторая задача", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Task task3 = new Task(1, "Первая задача (Upd)", "Обновление первой задачи", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task3);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", "Тестовая подзадача", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>(List.of(subtask1, subtask2));
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", subtasks);
        taskManager.addEpic(epic);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        taskManager.deleteSubtaskById(subtask2.getId());


        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile("data.save");
        System.out.println("Tasks: " + manager.getAllTasks());
        System.out.println("Epics: " + manager.getAllEpics());
        System.out.println("Subtasks: " + manager.getAllSubtasks());
        System.out.println("History: " + manager.getHistory());
    }
}
