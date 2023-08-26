package main.java.com.practikum.kanban.API.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.TaskValidateException;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.API.Enums.ServerResponseStatus;
import main.java.com.practikum.kanban.API.utils.Parser;
import main.java.com.practikum.kanban.API.utils.ServerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Subtasks implements HttpHandler {
    private final TaskManager taskManager;

    public Subtasks(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
            case "DELETE":
                getSubtasksHandler(exchange);
                return;
            case "POST":
                postSubtasksHandler(exchange);
                return;
            default:
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Такого эндпоинта не существует");
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
        }
    }

    private void getSubtasksHandler(HttpExchange exchange) throws IOException {
        HashMap<String, String> query = Parser.parseQuery(exchange.getRequestURI().getQuery());

        if (query.isEmpty() || !query.containsKey("id")) {
            if (exchange.getRequestMethod().equals("GET")) {
                ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

                List<String> subtasksJson = subtasks.stream()
                        .map(Subtask::toJson)
                        .collect(Collectors.toList());

                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, subtasksJson.toString());
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
            }
            else if (exchange.getRequestMethod().equals("DELETE")) {
                taskManager.deleteAllSubtasks();

                ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
            }
        } else {
            Optional<Integer> optionalId = Parser.parseId(exchange, query.get("id"));
            if (optionalId.isEmpty()) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Невалидный параметр \"id\"");
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 400);
                return;
            }

            int id = optionalId.get();
            String[] urlParts = exchange.getRequestURI().getPath().split("/");

            if (urlParts.length == 4 && urlParts[3].equals("epic")) {
                Epic epic = taskManager.getEpicById(id);
                if (epic == null) {
                    ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, String.format("Эпик с данным id (id = %d) не найден", id));
                    ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
                    return;
                }

                if (exchange.getRequestMethod().equals("GET")) {
                    ArrayList<Subtask> subtasks = epic.getSubtasks();

                    List<String> subtasksJson = subtasks.stream()
                            .map(Subtask::toJson)
                            .collect(Collectors.toList());

                    ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, subtasksJson.toString());
                    ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
                }
                else if (exchange.getRequestMethod().equals("DELETE")) {
                    taskManager.deleteAllSubtasksByEpicId(id);

                    ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                    ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
                }
            } else {
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask == null) {
                    ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, String.format("Подзадача с данным id (id = %d) не найдена", id));
                    ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
                    return;
                }
                if (exchange.getRequestMethod().equals("GET")) {
                    ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, subtask.toJson());
                    ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
                }
                else if (exchange.getRequestMethod().equals("DELETE")) {
                    taskManager.deleteSubtaskById(id);

                    ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                    ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
                }
            }
        }
    }

    private void postSubtasksHandler(HttpExchange exchange) throws IOException {
        String json = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(""));

        Subtask newSubtask = null;
        try {
            newSubtask = Subtask.fromJson(json);
        } catch (JsonParseException | TaskValidateException e) {
            ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, e.getMessage());
            ServerResponse.writeResponse(exchange, serverResponse.toJson(), 400);
            return;
        }

        Epic epic = taskManager.getEpicById(newSubtask.getEpicId());
        if (epic == null) {
            ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, String.format("Эпик с данным id (id = %d) не найден", newSubtask.getEpicId()));
            ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
            return;
        }

        if (newSubtask.getId() != -1) {
            Subtask findSubtask = taskManager.getSubtaskById(newSubtask.getId());
            if (findSubtask != null) {
                taskManager.updateSubtask(findSubtask.getId(), newSubtask);
            } else {
                newSubtask.setId(-1);
                taskManager.addSubtask(epic.getId(), newSubtask);
            }
        } else {
            taskManager.addSubtask(epic.getId(), newSubtask);
        }

        taskManager.addSubtask(newSubtask.getEpicId(), newSubtask);
        ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
        ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
    }
}
