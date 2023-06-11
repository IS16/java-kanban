package com.practikum.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {
    private int curId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int getCurId() {
        return ++this.curId;
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(int taskId) {
        return tasks.getOrDefault(taskId, null);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(int epicId) {
        return epics.getOrDefault(epicId, null);
    }

    public void updateEpic(Epic epic) {
        if  (!epics.containsKey(epic.getId())) {
            return;
        }

        Epic oldEpic = epics.get(epic.getId());
        Epic newEpic = new Epic(epic.getId(), epic.getTitle(), oldEpic.getSubtasks());
        epics.put(epic.getId(), newEpic);
    }

    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        deleteAllSubtasksByEpicId(epicId);
        epics.remove(epicId);
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void addSubtask(int epicId, Subtask subtask) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        subtask.setEpicId(epicId);
        epics.get(epicId).addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        return !epics.containsKey(epicId) ? new ArrayList<>() : epics.get(epicId).getSubtasks();
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.getOrDefault(subtaskId, null);
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }

        subtask.setEpicId(subtasks.get(subtask.getId()).getEpicId());
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtask(subtask);
    }

    public void deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }

        Subtask subtask = subtasks.get(subtaskId);
        epics.get(subtask.getEpicId()).deleteSubtaskById(subtaskId);
        subtasks.remove(subtaskId);
    }

    public void deleteAllSubtasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        ArrayList<Subtask> subtasksArray = epics.get(epicId).getSubtasks();
        for (Subtask subtask : subtasksArray) {
            epics.get(epicId).deleteSubtaskById(subtask.getId());
            subtasks.remove(subtask.getId());
        }
    }

    public void deleteAllSubtasks() {
        Iterator<Integer> iter = subtasks.keySet().iterator();
        while (iter.hasNext()) {
            Integer subtaskId = iter.next();
            Subtask subtask = subtasks.get(subtaskId);
            epics.get(subtask.getEpicId()).deleteSubtaskById(subtaskId);
            iter.remove();
        }
    }
}
