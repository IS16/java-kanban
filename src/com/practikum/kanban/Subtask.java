package com.practikum.kanban;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String title, String status) {
        super(id, title, status);
    }

    public Subtask(int id, String title, String description, String status) {
        super(id, title, description, status);
    }

    protected void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
    }

    @Override
    public String toString() {
        return "Subtask(Id = " + super.getId() + ", epicId=" + epicId + ", Title=\"" + super.getTitle() + "\", Description=\"" + super.getDescription() + "\", Status=\"" + super.getStatus() + "\")";
    }
}
