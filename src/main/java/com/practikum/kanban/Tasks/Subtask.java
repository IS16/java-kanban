package main.java.com.practikum.kanban.Tasks;

import com.google.gson.JsonElement;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.TaskValidateException;
import main.java.com.practikum.kanban.API.utils.Parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, TaskStatus status) {
        super(title, "", status);
        super.setType(TaskType.SUBTASK);
    }

    public Subtask(String title, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, "", status, startTime, duration);
        super.setType(TaskType.SUBTASK);
    }

    public Subtask(int epicId, String title, TaskStatus status) {
        super(title, "", status);
        super.setType(TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, "", status, startTime, duration);
        super.setType(TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, TaskStatus status) {
        super(title, description, status);
        super.setType(TaskType.SUBTASK);
    }

    public Subtask(String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
        super.setType(TaskType.SUBTASK);
    }

    public Subtask(int epicId, String title, String description, TaskStatus status) {
        super(title, description, status);
        super.setType(TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(int epicId, String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
        super.setType(TaskType.SUBTASK);
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
            return String.format("%d,%s,%s,%s,%s,-,-,%d", super.getId(), super.getType(), super.getTitle(), super.getStatus(), super.getDescription(), epicId);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");
            return String.format("%d,%s,%s,%s,%s,%s,%d,%d", super.getId(), super.getType(), super.getTitle(), super.getStatus(), super.getDescription(), super.getStartTime().format(formatter), super.getDuration(), epicId);
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

    public String toJson() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("{\"id\":%d,\"type\":\"%s\",\"epicId\":%d,\"title\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"", super.getId(), super.getType(), epicId, super.getTitle(), super.getDescription(), super.getStatus()));

        if (super.getStartTime() == null) {
            out.append(",\"startTime\":\"null\"");
        } else {
            out.append(String.format(",\"startTime\":\"%s\"", super.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"))));
        }

        out.append(String.format(",\"duration\":%d}", super.getDuration()));

        return out.toString();
    }

    public static Subtask fromJson(String json) throws JsonParseException {
        Map<String, JsonElement> jsonMap = Parser.parseJson(json);

        if (!jsonMap.containsKey("title")) {
            throw new TaskValidateException("Не передан параметр \"title\"");
        }

        if (!jsonMap.containsKey("status")) {
            throw new TaskValidateException("Не передан параметр \"status\"");
        }

        if (!jsonMap.containsKey("epicId")) {
            throw new TaskValidateException("Не передан параметр \"epicId\"");
        }

        try {
            int epicId = jsonMap.get("epicId").getAsInt();
        } catch (Exception e) {
            throw new TaskValidateException("Невалидный параметр \"epicId\"");
        }

        Subtask newSubtask = null;
        try {
            newSubtask = new Subtask(jsonMap.get("epicId").getAsInt(), jsonMap.get("title").getAsString(), TaskStatus.valueOf(jsonMap.get("status").getAsString()));
        } catch (Exception e) {
            throw new TaskValidateException("Невалидный параметр \"status\"");
        }

        if (jsonMap.containsKey("id")) {
            try {
                newSubtask.setId(jsonMap.get("id").getAsInt());
            } catch (Exception e) {
                throw new TaskValidateException("Невалидный параметр \"id\"");
            }
        }

        if (jsonMap.containsKey("description")) {
            newSubtask.setDescription(jsonMap.get("description").getAsString());
        }

        if (jsonMap.containsKey("startTime") && !jsonMap.get("startTime").getAsString().equals("null")) {
            try {
                LocalDateTime startTime = LocalDateTime.parse(jsonMap.get("startTime").getAsString(), DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
                newSubtask.setStartTime(startTime);

                if (jsonMap.containsKey("duration") && !jsonMap.get("duration").getAsString().equals("0")) {
                    try {
                        newSubtask.setDuration(jsonMap.get("duration").getAsInt());
                    } catch (Exception e) {
                        throw new JsonParseException("Длительность должна быть целым числом", 0);
                    }
                }

            } catch (Exception e) {
                throw new JsonParseException("Невалидная дата начала", 0);
            }
        }

        return newSubtask;
    }

    public static ArrayList<Subtask> fromEpicJson(String json) throws JsonParseException {
        Map<String, JsonElement> jsonMap = Parser.parseJson(json);
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (jsonMap.containsKey("subtasks")) {
            List<JsonElement> subtasksJson = jsonMap.get("subtasks").getAsJsonArray().asList();

            for (JsonElement elem : subtasksJson) {
                try {
                    subtasks.add(Subtask.fromJson(elem.toString()));
                } catch (JsonParseException e) {
                    throw new JsonParseException("Невалидный параметр \"subtasks\"", 0);
                }
            }
        }

        return subtasks;
    }
}
