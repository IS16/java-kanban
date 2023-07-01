package com.practikum.kanban.Managers.TaskManager;

import com.practikum.kanban.Managers.HistoryManager.HistoryManager;
import com.practikum.kanban.Tasks.Epic;
import com.practikum.kanban.Tasks.Subtask;
import com.practikum.kanban.Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class InMemoryTaskManager implements TaskManager {
    private int curId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public int getCurId() {
        return ++this.curId;
    }

    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.getOrDefault(taskId, null);
        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        for(Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }

        tasks.clear();
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.getOrDefault(epicId, null);
        if (epic != null) {
            historyManager.add(epic);
        }

        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if  (!epics.containsKey(epic.getId())) {
            return;
        }

        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic.getSubtasks().size() != epic.getSubtasks().size()) {
            return;
        }

        for (Subtask subtask : oldEpic.getSubtasks()) {
            if (!epic.hasSubtask(subtask.getId())) {
                return;
            }
        }

        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        historyManager.remove(epicId);
        deleteAllSubtasksByEpicId(epicId);
        epics.remove(epicId);
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }

        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void addSubtask(int epicId, Subtask subtask) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        subtask.setEpicId(epicId);
        epics.get(epicId).addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        return !epics.containsKey(epicId) ? new ArrayList<>() : epics.get(epicId).getSubtasks();
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.getOrDefault(subtaskId, null);
        if (subtask != null) {
            historyManager.add(subtask);
        }

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            return;
        }

        if (!epics.get(subtask.getEpicId()).hasSubtask(subtask.getId())) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtask(subtask);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }

        historyManager.remove(subtaskId);
        Subtask subtask = subtasks.get(subtaskId);
        epics.get(subtask.getEpicId()).deleteSubtaskById(subtaskId);
        subtasks.remove(subtaskId);
    }

    @Override
    public void deleteAllSubtasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        ArrayList<Subtask> subtasksArray = epics.get(epicId).getSubtasks();
        for (Subtask subtask : subtasksArray) {
            historyManager.remove(subtask.getId());

            epics.get(epicId).deleteSubtaskById(subtask.getId());
            subtasks.remove(subtask.getId());
        }
    }

    @Override
    public void deleteAllSubtasks() {
        Iterator<Integer> iter = subtasks.keySet().iterator();
        while (iter.hasNext()) {
            Integer subtaskId = iter.next();
            Subtask subtask = subtasks.get(subtaskId);
            epics.get(subtask.getEpicId()).deleteSubtaskById(subtaskId);
            historyManager.remove(subtaskId);

            iter.remove();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
