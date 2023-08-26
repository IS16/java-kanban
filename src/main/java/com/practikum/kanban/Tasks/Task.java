package main.java.com.practikum.kanban.Tasks;

import com.google.gson.JsonElement;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.TaskValidateException;
import main.java.com.practikum.kanban.API.utils.Parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Task {
    private int id = -1;
    private TaskType type = TaskType.TASK;
    private String title;
    private String description = "";
    private LocalDateTime startTime;
    private int duration;
    private TaskStatus status = TaskStatus.NEW;

    public Task(String title) {
        this.title = title;
    }

    public Task(int id, String title, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, LocalDateTime startTime, int duration) {
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return this.id;
    }

    public TaskType getType() {
        return this.type;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    protected void setTitle(String title) {
        this.title = title;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
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

        Task newTask = new Task(parts[2]);
        newTask.setId(Integer.parseInt(parts[0]));
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

    public String toJson() {
        StringBuilder out = new StringBuilder();
        out.append(String.format("{\"id\":%d,\"type\":\"%s\",\"title\":\"%s\",\"description\":\"%s\",\"status\":\"%s\"", id, type, title, description, status));

        if (startTime == null) {
            out.append(",\"startTime\":\"null\"");
        } else {
            out.append(String.format(",\"startTime\":\"%s\"", startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"))));
        }

        out.append(String.format(",\"duration\":%d}", duration));

        return out.toString();
    }

    public static Task fromJson(String json) throws JsonParseException {
        Map<String, JsonElement> jsonMap = Parser.parseJson(json);

        if (!jsonMap.containsKey("title")) {
            throw new TaskValidateException("Не передан параметр \"title\"");
        }

        Task newTask = new Task(jsonMap.get("title").getAsString());

        if (jsonMap.containsKey("id")) {
            try {
                newTask.setId(jsonMap.get("id").getAsInt());
            } catch (Exception e) {
                throw  new TaskValidateException("Невалидный параметр \"id\"");
            }
        }

        if (jsonMap.containsKey("description")) {
            newTask.setDescription(jsonMap.get("description").getAsString());
        }

        if (jsonMap.containsKey("status")) {
            try {
                newTask.setStatus(TaskStatus.valueOf(jsonMap.get("status").getAsString()));
            } catch (Exception e) {
                throw  new TaskValidateException("Невалидный параметр \"status\"");
            }
        }

        if (jsonMap.containsKey("startTime") && !jsonMap.get("startTime").getAsString().equals("null")) {
            try {
                LocalDateTime startTime = LocalDateTime.parse(jsonMap.get("startTime").getAsString(), DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss"));
                newTask.setStartTime(startTime);

                if (jsonMap.containsKey("duration") && !jsonMap.get("duration").getAsString().equals("0")) {
                    try {
                        newTask.setDuration(jsonMap.get("duration").getAsInt());
                    } catch (Exception e) {
                        throw new JsonParseException("Длительность должна быть целым числом", 0);
                    }
                }
            } catch (Exception e) {
                throw new JsonParseException("Невалидная дата начала", 0);
            }
        }

        return newTask;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
