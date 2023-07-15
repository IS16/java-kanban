package com.practikum.kanban.Tasks;

public class Subtask extends Task {
    private int epicId;
    private final TaskType type = TaskType.SUBTASK;

    public Subtask(int id, String title, TaskStatus status) {
        super(id, title, "", status);
    }

    public Subtask(int id, int epicId, String title, TaskStatus status) {
        super(id, title, "", status);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, TaskStatus status) {
        super(id, title, description, status);
    }

    public Subtask(int id, int epicId, String title, String description, TaskStatus status) {
        super(id, title, description, status);
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
        return String.format("%d,%s,%s,%s,%s,%d", super.getId(), type, super.getTitle(), super.getStatus(), super.getDescription(), epicId);
    }

    public static Subtask fromString(String value) {
        String[] parts = value.split(",");
        return new Subtask(Integer.parseInt(parts[0]), Integer.parseInt(parts[5]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
    }
}
