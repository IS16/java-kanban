package main.java.com.practikum.kanban.Managers.TaskManager;

import com.google.gson.*;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.ManagerSaveException;
import main.java.com.practikum.kanban.KVServer.KVTaskClient;
import main.java.com.practikum.kanban.Managers.HistoryManager.HistoryManager;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {
    private final KVTaskClient client;

    public HttpTaskManager(String url, HistoryManager historyManager) throws IOException, InterruptedException {
        super(historyManager, url);
        this.client = new KVTaskClient(url);
    }

    public static HttpTaskManager loadFromDB(String url, HistoryManager historyManager) throws IOException, InterruptedException, JsonParseException {
        KVTaskClient client = new KVTaskClient(url);
        HttpTaskManager manager = new HttpTaskManager(url, historyManager);

        HashMap<Integer, Task> entities = new HashMap<>();
        Integer maxId = 0;

        String epicsJSON = client.get("epics");
        JsonArray epicsArray = new JsonParser().parse(epicsJSON).getAsJsonArray();

        String subtasksJSON = client.get("subtasks");
        JsonArray subtasksArray = new JsonParser().parse(subtasksJSON).getAsJsonArray();

        String tasksJSON = client.get("tasks");
        JsonArray tasksArray = new JsonParser().parse(tasksJSON).getAsJsonArray();
        ArrayList<Integer> ids;

        try {
            ids = HttpTaskManager.historyFromString(client.get("history"));
        } catch (Exception e) {
            ids = new ArrayList<>();
        }

        for (JsonElement elem : epicsArray) {
            Epic epic = Epic.fromJson(elem.toString());
            entities.put(epic.getId(), epic);

            manager.epics.put(epic.getId(), epic);

            maxId = Math.max(epic.getId(), maxId);
        }

        for (JsonElement elem : subtasksArray) {
            Subtask subtask = Subtask.fromJson(elem.toString());
            entities.put(subtask.getId(), subtask);

            manager.epics.get(subtask.getEpicId()).addSubtask(subtask);
            manager.subtasks.put(subtask.getId(), subtask);
            manager.prioritiezedTasks.add(subtask);

            maxId = Math.max(subtask.getId(), maxId);
        }

        for (JsonElement elem : tasksArray) {
            Task task = Task.fromJson(elem.toString());
            entities.put(task.getId(), task);

            manager.tasks.put(task.getId(), task);
            manager.prioritiezedTasks.add(task);

            maxId = Math.max(task.getId(), maxId);
        }

        for (Integer id : ids) {
            manager.historyManager.add(entities.get(id));
        }

        manager.curId = maxId;
        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
    }

    @Override
    public Task getTaskById(int taskId) {
        return super.getTaskById(taskId);
    }

    @Override
    public void updateTask(int taskId, Task task) {
        super.updateTask(taskId, task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
    }

    @Override
    public Epic getEpicById(int epicId) {
        return super.getEpicById(epicId);
    }

    @Override
    public void updateEpic(int epicId, Epic epic) {
        super.updateEpic(epicId, epic);
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        super.addSubtask(epicId, subtask);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        return super.getSubtaskById(subtaskId);
    }

    @Override
    public void updateSubtask(int subtaskId, Subtask subtask) {
        super.updateSubtask(subtaskId, subtask);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
    }

    @Override
    public void deleteAllSubtasksByEpicId(int epicId) {
        super.deleteAllSubtasksByEpicId(epicId);
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Override
    protected void save() {
        try {
            ArrayList<Task> tasks = super.getAllTasks();
            List<String> tasksJson = tasks.stream()
                    .map(Task::toJson)
                    .collect(Collectors.toList());

            ArrayList<Subtask> subtasks = super.getAllSubtasks();
            List<String> subtasksJson = subtasks.stream()
                    .map(Subtask::toJson)
                    .collect(Collectors.toList());

            ArrayList<Epic> epics = super.getAllEpics();
            List<String> epicsJson = epics.stream()
                    .map(Epic::toJson)
                    .collect(Collectors.toList());

            client.put("tasks", tasksJson.toString());
            client.put("subtasks", subtasksJson.toString());
            client.put("epics", epicsJson.toString());
            client.put("history", HttpTaskManager.historyToString(super.historyManager));
        } catch (IOException | InterruptedException e) {
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
