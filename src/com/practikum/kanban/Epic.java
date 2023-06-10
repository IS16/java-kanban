package com.practikum.kanban;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(int id, String title, ArrayList<Subtask> subtasks) {
        super(id, title, "NEW");

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
    }

    public Epic(int id, String title, String description, ArrayList<Subtask> subtasks) {
        super(id, title, description, "NEW");

        for (Subtask item : subtasks) {
            item.setEpicId(this.getId());
            this.subtasks.put(item.getId(), item);
        }

        updateStatus();
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> out = new ArrayList<>();

        for (int subtaskId : subtasks.keySet()) {
            out.add(subtasks.get(subtaskId));
        }

        return out;
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
        ArrayList<Subtask> subtaskArrayList = new ArrayList<>();

        for (Integer id : subtasks.keySet()) {
            subtaskArrayList.add(subtasks.get(id));
        }

        return "Epic(Id="+ super.getId() + ", Title=\"" + super.getTitle() + "\", Description=\"" + super.getDescription() + "\", Status=\"" + super.getStatus() + "\", Subtasks=" + subtaskArrayList + ")";
    }
}
