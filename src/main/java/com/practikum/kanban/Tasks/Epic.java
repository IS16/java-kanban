package main.java.com.practikum.kanban.Tasks;

import com.google.gson.JsonElement;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.TaskValidateException;
import main.java.com.practikum.kanban.API.utils.Parser;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private LocalDateTime endTime;

    public Epic(String title) {
        super(title);
        super.setType(TaskType.EPIC);
        calculateParams();
    }

    public Epic(String title, String description) {
        super(title, description);
        super.setType(TaskType.EPIC);
        calculateParams();
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calculateParams();

    }

    public boolean hasSubtask(int subtaskId) {
        return subtasks.containsKey(subtaskId);
    }

    public void calculateParams() {
        updateStatus();
        updateStartEndTime();
        updateDuration();
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        calculateParams();
    }

    public void deleteSubtaskById(int subtaskId) {
        subtasks.remove(subtaskId);
        calculateParams();
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
    public void setTitle(String title) {
        super.setTitle(title);
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    @Override
    public void setStatus(TaskStatus status) { }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", super.getId(), super.getType(), super.getTitle(), super.getStatus(), super.getDescription());
    }

    public static Epic fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 3) {
            return null;
        }

        Epic newEpic = new Epic(parts[2]);
        newEpic.setId(Integer.parseInt(parts[0]));
        if (parts.length > 4) {
           newEpic.setDescription(parts[4]);
        }

        return newEpic;
    }

    public String toJson() {
        StringBuilder out = new StringBuilder();

        out.append(String.format("{\"id\":%d,\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"subtasks\":[", super.getId(), super.getType(), super.getTitle(), super.getDescription()));

        String subtasksArr = subtasks.values().stream()
                .map(Subtask::toJson)
                .collect(Collectors.joining(","));

        out.append(String.format("%s]}", subtasksArr));

        return out.toString();
    }

    public static Epic fromJson(String json) throws JsonParseException {
        Map<String, JsonElement> jsonMap = Parser.parseJson(json);

        if (!jsonMap.containsKey("title")) {
            throw new TaskValidateException("Не передан параметр \"title\"");
        }

        Epic newEpic = new Epic(jsonMap.get("title").getAsString());

        if (jsonMap.containsKey("id")) {
            try {
                newEpic.setId(jsonMap.get("id").getAsInt());
            } catch (Exception e) {
                throw new TaskValidateException("Невалидный параметр \"id\"");
            }
        }

        if (jsonMap.containsKey("description")) {
            newEpic.setDescription(jsonMap.get("description").getAsString());
        }

        return newEpic;
    }
}
