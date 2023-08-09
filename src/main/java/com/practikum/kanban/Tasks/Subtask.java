package main.java.com.practikum.kanban.Tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(String title, TaskStatus status) {
        super(title, "", status);
    }

    public Subtask(String title, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, "", status, startTime, duration);
    }

    public Subtask(int epicId, String title, TaskStatus status) {
        super(title, "", status);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, "", status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status) {
        super(title, description, status);
    }

    public Subtask(String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
    }

    public Subtask(int epicId, String title, String description, TaskStatus status) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
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
            Subtask subtask = new Subtask(Integer.parseInt(parts[7]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
            subtask.setId(Integer.parseInt(parts[0]));
            return subtask;
        } else {
            Subtask subtask = new Subtask(Integer.parseInt(parts[7]), parts[2], parts[4], TaskStatus.valueOf(parts[3]), LocalDateTime.parse(parts[5], formatter), Integer.parseInt(parts[6]));
            subtask.setId(Integer.parseInt(parts[0]));
            return subtask;
        }
    }
}
