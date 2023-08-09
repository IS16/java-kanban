package main.java.com.practikum.kanban.Managers.TaskManager;

import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    private int getCurId() {
        return 0;
    }

    void addTask(Task task);
    ArrayList<Task> getAllTasks();
    Task getTaskById(int taskId);
    void updateTask(int taskId, Task task);
    void deleteTaskById(int taskId);
    void deleteAllTasks();

    void addEpic(Epic epic);
    ArrayList<Epic> getAllEpics();
    Epic getEpicById(int epicId);
    void updateEpic(int epicId, Epic epic);
    void deleteEpicById(int epicId);
    void deleteAllEpics();

    void addSubtask(int epicId, Subtask subtask);

    void addManySubtasks(int epicId, ArrayList<Subtask> subtasks);
    ArrayList<Subtask> getAllSubtasks();
    ArrayList<Subtask> getAllSubtasksByEpicId(int epicId);
    Subtask getSubtaskById(int subtaskId);
    void updateSubtask(int subtaskId, Subtask subtask);
    void deleteSubtaskById(int subtaskId);
    void deleteAllSubtasksByEpicId(int epicId);
    void deleteAllSubtasks();

    ArrayList<Task> getHistory();

    ArrayList<Task> getPrioritizedTasks();
}
