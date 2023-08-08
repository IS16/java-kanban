package com.practikum.kanban.Managers.TaskManager;

import com.practikum.kanban.Exceptions.TaskValidateException;
import com.practikum.kanban.Managers.HistoryManager.HistoryManager;
import com.practikum.kanban.Tasks.Epic;
import com.practikum.kanban.Tasks.Subtask;
import com.practikum.kanban.Tasks.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int curId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected final HistoryManager historyManager;

    private final Comparator<Task> tasksComparator = new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() == null) {
                return 1;
            } else if (o2.getStartTime() == null) {
                return -1;
            }

            return o1.getStartTime().compareTo(o2.getStartTime());
        }
    };

    protected final TreeSet<Task> prioritiezedTasks = new TreeSet<>(tasksComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    private void validateTaskTime(Task inputTask) {
        if (inputTask.getStartTime() == null) {
            return;
        }

        List<Task> onlyTasksAndSubtasks = prioritiezedTasks.stream()
                .filter(item -> (item.getStartTime() != null) && (item.getClass() != Epic.class))
                .collect(Collectors.toList());

        for (Task task: onlyTasksAndSubtasks) {
            boolean isStartTimeBetweenExistStartAndEnd = task.getStartTime().isBefore(inputTask.getStartTime()) && task.getEndTime().isAfter(inputTask.getStartTime());
            boolean isEndTimeBetweenExistStartAndEnd = task.getStartTime().isBefore(inputTask.getEndTime()) && task.getEndTime().isAfter(inputTask.getEndTime());
            boolean isTaskStartBeforeExistStartAndEndAndEndAfter = task.getStartTime().isAfter(inputTask.getStartTime()) && task.getEndTime().isBefore(inputTask.getEndTime());
            boolean isTaskBetweenExistStartAndEnd = task.getStartTime().isBefore(inputTask.getStartTime()) && task.getEndTime().isAfter(inputTask.getEndTime());

            if (isStartTimeBetweenExistStartAndEnd || isEndTimeBetweenExistStartAndEnd || isTaskStartBeforeExistStartAndEndAndEndAfter || isTaskBetweenExistStartAndEnd) {
                throw new TaskValidateException(String.format("Задача пересекается с другой задачей (ID: %d)", task.getId()));
            }
        }
    }

    @Override
    public int getCurId() {
        return ++this.curId;
    }

    @Override
    public void addTask(Task task) {
        validateTaskTime(task);
        tasks.put(task.getId(), task);
        prioritiezedTasks.add(task);
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
        validateTaskTime(task);
        prioritiezedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        prioritiezedTasks.add(task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        prioritiezedTasks.remove(tasks.get(taskId));
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        for(Integer id : tasks.keySet()) {
            prioritiezedTasks.remove(tasks.get(id));
            historyManager.remove(id);
        }

        tasks.clear();
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        for (Subtask subtask : epic.getSubtasks()) {
            validateTaskTime(subtask);
            prioritiezedTasks.add(subtask);
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
        oldEpic.setTitle(epic.getTitle());
        oldEpic.setDescription(epic.getDescription());

        epics.put(epic.getId(), oldEpic);
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
            prioritiezedTasks.remove(subtasks.get(id));
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

        validateTaskTime(subtask);
        subtask.setEpicId(epicId);
        epics.get(epicId).addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritiezedTasks.add(subtask);
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

        validateTaskTime(subtask);
        prioritiezedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        prioritiezedTasks.add(subtask);
        epics.get(subtask.getEpicId()).updateSubtask(subtask);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }

        historyManager.remove(subtaskId);
        Subtask subtask = subtasks.get(subtaskId);
        prioritiezedTasks.remove(subtask);
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
            prioritiezedTasks.remove(subtask);
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
            prioritiezedTasks.remove(subtask);
            epics.get(subtask.getEpicId()).deleteSubtaskById(subtaskId);
            historyManager.remove(subtaskId);

            iter.remove();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritiezedTasks);
    }
}
