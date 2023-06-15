package com.practikum.kanban;

import java.util.ArrayList;

public interface TaskManager {
    int getCurId();

    void addTask(Task task);
    ArrayList<Task> getAllTasks();
    Task getTaskById(int taskId);
    void updateTask(Task task);
    void deleteTaskById(int taskId);
    void deleteAllTasks();

    void addEpic(Epic epic);
    ArrayList<Epic> getAllEpics();
    Epic getEpicById(int epicId);
    void updateEpic(Epic epic);
    void deleteEpicById(int epicId);
    void deleteAllEpics();

    void addSubtask(int epicId, Subtask subtask);
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Subtask> getAllSubtasksByEpicId(int epicId);
    Subtask getSubtaskById(int subtaskId);
    void updateSubtask(Subtask subtask);
    void deleteSubtaskById(int subtaskId);
    void deleteAllSubtasksByEpicId(int epicId);
    void deleteAllSubtasks();

    ArrayList<Task> getHistory();
}
