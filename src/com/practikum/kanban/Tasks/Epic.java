package com.practikum.kanban.Tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final TaskType type = TaskType.EPIC;
    private LocalDateTime endTime;

    public Epic(int id, String title) {
        super(id, title);
        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public Epic(int id, String title, ArrayList<Subtask> subtasks) {
        super(id, title);

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public Epic(int id, String title, String description, ArrayList<Subtask> subtasks) {
        super(id, title, description);

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public boolean hasSubtask(int subtaskId) {
        return subtasks.containsKey(subtaskId);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public void deleteSubtaskById(int subtaskId) {
        subtasks.remove(subtaskId);
        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            super.setStatus(TaskStatus.NEW);
            return;
        }

        int newTasksAmount = 0;
        int doneTasksAmount = 0;

        for (Integer id: subtasks.keySet()) {
            if (subtasks.get(id).getStatus() == TaskStatus.NEW) {
                newTasksAmount++;
            } else if (subtasks.get(id).getStatus() == TaskStatus.DONE) {
                doneTasksAmount++;
            }
        }

        if (newTasksAmount == subtasks.size()) {
            super.setStatus(TaskStatus.NEW);
        } else if (doneTasksAmount == subtasks.size()) {
            super.setStatus(TaskStatus.DONE);
        } else {
            super.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateStartEndTime() {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Integer id: subtasks.keySet()) {
            if (startTime == null && subtasks.get(id).getStartTime() != null) {
                startTime = subtasks.get(id).getStartTime();
                endTime = subtasks.get(id).getEndTime();
            } else if (subtasks.get(id).getStartTime() != null) {
                if (startTime.isAfter(subtasks.get(id).getStartTime())){
                    startTime = subtasks.get(id).getEndTime();
                }

                if (endTime.isBefore(subtasks.get(id).getEndTime())) {
                    endTime = subtasks.get(id).getEndTime();
                }
            }
        }

        super.setStartTime(startTime);
        this.endTime = endTime;
    }

    private void updateDuration() {
        int sum = 0;
        for (Integer id: subtasks.keySet()) {
            sum += subtasks.get(id).getDuration();
        }

        super.setDuration(sum);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    protected void setStatus(TaskStatus status) { }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", super.getId(), type, super.getTitle(), super.getStatus(), super.getDescription());
    }

    public static Epic fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 3) {
            return null;
        }

        Epic newEpic = new Epic(Integer.parseInt(parts[0]), parts[2], new ArrayList<>());
        if (parts.length > 4) {
           newEpic.setDescription(parts[4]);
        }

        return newEpic;
    }
}
