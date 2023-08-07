package com.practikum.kanban.Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class Task {
    private final int id;
    private final TaskType type = TaskType.TASK;
    private String title;
    private String description = "";
    private LocalDateTime startTime;
    private int duration;
    private TaskStatus status = TaskStatus.NEW;

    public Task(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Task(int id, String title, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Task(int id, String title, String description, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public int getDuration() {
        return this.duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return this.startTime.plusMinutes(this.duration);
        }

        return null;
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

    protected void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        if (startTime == null) {
            return String.format("%d,%s,%s,%s,%s,-,-", id, type, title, status, description);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");
            return String.format("%d,%s,%s,%s,%s,%s,%d", id, type, title, status, description, startTime.format(formatter), duration);
        }
    }

    public static Task fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");

        String[] parts = value.split(",");
        if (parts.length < 4) {
            return null;
        }

        Task newTask = new Task(Integer.parseInt(parts[0]), parts[2]);
        newTask.setStatus(TaskStatus.valueOf(parts[3]));
        newTask.setDescription(parts[4]);

        if (parts.length > 6) {
            if (!parts[5].equals("-")) {
                newTask.setStartTime(LocalDateTime.parse(parts[5], formatter));
                newTask.setDuration(Integer.parseInt(parts[6]));
            }
        }

        return newTask;
    }
}
