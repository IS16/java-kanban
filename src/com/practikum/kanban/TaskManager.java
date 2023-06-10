package com.practikum.kanban;

import java.util.ArrayList;
import java.util.HashMap;

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
        ArrayList<Task> out = new ArrayList<>();

        for (int taskId : tasks.keySet()) {
            out.add(tasks.get(taskId));
        }

        return out;
    }

    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            return tasks.get(taskId);
        }

        return null;
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
        ArrayList<Epic> out = new ArrayList<>();

        for (int epicId : epics.keySet()) {
            out.add(epics.get(epicId));
        }

        return out;
    }

    public Epic getEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId);
        }

        return null;
    }

    public void updateEpic(Epic epic) {
        Object[] ids = subtasks.keySet().toArray();

        for (Object id : ids) {
            Subtask subtask = subtasks.get((int) id);
            if (subtask.getEpicId() == epic.getId()) {
                subtasks.remove((int) id);
            }
        }

        epics.put(epic.getId(), epic);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.put(subtask.getId(), subtask);
        }
    }

    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }

        Object[] ids = subtasks.keySet().toArray();

        for (Object id : ids) {
            Subtask subtask = subtasks.get((int) id);
            if (subtask.getEpicId() == epicId) {
                subtasks.remove((int) id);
            }
        }

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
        ArrayList<Subtask> out = new ArrayList<>();

        for (int subtaskId : subtasks.keySet()) {
            out.add(subtasks.get(subtaskId));
        }

        return out;
    }

    public ArrayList<Subtask> getAllSubtasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            return new ArrayList<>();
        }

        return epics.get(epicId).getSubtasks();
    }

    public Subtask getSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return null;
        }

        return subtasks.get(subtaskId);
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
        Object[] ids = subtasks.keySet().toArray();
        for (Object id : ids) {
            Subtask subtask = subtasks.get((int) id);
            epics.get(subtask.getEpicId()).deleteSubtaskById((int) id);
            subtasks.remove((int) id);
        }
    }
}
