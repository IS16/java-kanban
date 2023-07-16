package com.practikum.kanban.Tasks;

public class Task {
    private final int id;
    private final TaskType type = TaskType.TASK;
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
        return String.format("%d,%s,%s,%s,%s", id, type, title, status, description);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 4) {
            return null;
        }

        Task newTask = new Task(Integer.parseInt(parts[0]), parts[2]);
        newTask.setStatus(TaskStatus.valueOf(parts[3]));

        if (parts.length > 4) {
            newTask.setDescription(parts[4]);
        }

        return newTask;
    }
}
