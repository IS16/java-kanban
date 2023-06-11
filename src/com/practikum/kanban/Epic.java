package com.practikum.kanban;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(int id, String title) {
        super(id, title);
        updateStatus();
    }

    public Epic(int id, String title, ArrayList<Subtask> subtasks) {
        super(id, title);

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
    }

    public Epic(int id, String title, String description, ArrayList<Subtask> subtasks) {
        super(id, title, description);

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }

    public void deleteSubtaskById(int subtaskId) {
        subtasks.remove(subtaskId);
        updateStatus();
    }

    private void updateStatus() {
        if (subtasks.isEmpty()) {
            super.setStatus("NEW");
            return;
        }

        int newTasksAmount = 0;
        int doneTasksAmount = 0;

        for (Integer id : subtasks.keySet()) {
            if (subtasks.get(id).getStatus().equals("NEW")) {
                newTasksAmount++;
            }

            if (subtasks.get(id).getStatus().equals("DONE")) {
                doneTasksAmount++;
            }
        }

        if (newTasksAmount == subtasks.size()) {
            super.setStatus("NEW");
        } else if (doneTasksAmount == subtasks.size()) {
            super.setStatus("DONE");
        } else {
            super.setStatus("IN_PROGRESS");
        }
    }

    @Override
    protected void setStatus(String status) { }

    @Override
    public String toString() {
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>(subtasks.values());

        return "Epic(Id="+ super.getId() + ", Title=\"" + super.getTitle() + "\", Description=\"" + super.getDescription() + "\", Status=\"" + super.getStatus() + "\", Subtasks=" + subtaskArrayList + ")";
    }
}
