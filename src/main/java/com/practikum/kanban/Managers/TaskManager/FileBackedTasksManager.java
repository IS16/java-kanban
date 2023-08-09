package main.java.com.practikum.kanban.Managers.TaskManager;

import main.java.com.practikum.kanban.Exceptions.ManagerSaveException;
import main.java.com.practikum.kanban.Managers.HistoryManager.HistoryManager;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        ArrayList<Subtask> subtasks = new ArrayList<>();
        int maxId = 0;

        for (String line : data.subList(1, data.size())) {
            if (line.isBlank()) {
                for (Subtask subtask : subtasks) {
                    fileBackedTasksManager.addSubtask(subtask.getEpicId(), subtask);
                }

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
                    subtasks.add(newEnitity);
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
    public void updateTask(int taskId, Task task) {
        super.updateTask(taskId, task);
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
    public void updateEpic(int epicId, Epic epic) {
        super.updateEpic(epicId, epic);
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
    public void updateSubtask(int subtaskId, Subtask subtask) {
        super.updateSubtask(subtaskId, subtask);
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
            bw.write("id,type,name,status,description,startTime,duration,epic\n");

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
}
