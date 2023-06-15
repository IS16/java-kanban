package com.practikum.kanban;

public class Task {
    private final int id;
    private String title;
    private String description = "";
    private TaskStatus status = TaskStatus.NEW;

    public Task(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task(Id = " + this.id + ", Title=\"" + this.title + "\", Description=\"" + this.description + "\", Status=\"" + this.status + "\")";
    }
}
