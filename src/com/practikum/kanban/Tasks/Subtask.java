package com.practikum.kanban.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(int id, String title, TaskStatus status) {
        super(id, title, "", status);
    }

    public Subtask(int id, String title, TaskStatus status, LocalDateTime startTime, int duration) {

        super(id, title, "", status, startTime, duration);
    }

    public Subtask(int id, int epicId, String title, TaskStatus status) {
        super(id, title, "", status);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String title, TaskStatus status, LocalDateTime startTime, int duration) {
        super(id, title, "", status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public Subtask(int id, String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(id, title, description, status, startTime, duration);
    }

    public Subtask(int id, int epicId, String title, String description, TaskStatus status) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, int epicId, String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(id, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
    }

    @Override
    public String toString() {
        if (super.getStartTime() == null) {
            return String.format("%d,%s,%s,%s,%s,-,-,%d", super.getId(), type, super.getTitle(), super.getStatus(), super.getDescription(), epicId);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");
            return String.format("%d,%s,%s,%s,%s,%s,%d,%d", super.getId(), type, super.getTitle(), super.getStatus(), super.getDescription(), super.getStartTime().format(formatter), super.getDuration(), epicId);
        }
    }

    public static Subtask fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");

        String[] parts = value.split(",");
        if (parts.length < 8) {
            return null;
        }

        if (parts[5].equals("-")) {
            return new Subtask(Integer.parseInt(parts[0]), Integer.parseInt(parts[7]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
        } else {
            return new Subtask(Integer.parseInt(parts[0]), Integer.parseInt(parts[7]), parts[2], parts[4], TaskStatus.valueOf(parts[3]), LocalDateTime.parse(parts[5], formatter), Integer.parseInt(parts[6]));
        }
    }
}
