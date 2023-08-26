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

public class Epics implements HttpHandler {
    private final TaskManager taskManager;

    public Epics(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
            case "DELETE":
                getEpicsHandler(exchange);
                return;
            case "POST":
                postEpicsHandler(exchange);
                return;
            default:
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Такого эндпоинта не существует");
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
        }
    }

    private void getEpicsHandler(HttpExchange exchange) throws IOException {
        HashMap<String, String> query = Parser.parseQuery(exchange.getRequestURI().getQuery());

        if (query.isEmpty() || !query.containsKey("id")) {
            if (exchange.getRequestMethod().equals("GET")) {
                ArrayList<Epic> epics = taskManager.getAllEpics();

                List<String> epicsJson = epics.stream()
                        .map(Epic::toJson)
                        .collect(Collectors.toList());

                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, epicsJson.toString());
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
            }
            else if (exchange.getRequestMethod().equals("DELETE")) {
                taskManager.deleteAllEpics();

                ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
            }
        }
        else {
            Optional<Integer> optionalId = Parser.parseId(exchange, query.get("id"));
            if (optionalId.isEmpty()) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Невалидный параметр \"id\"");
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 400);
                return;
            }

            int id = optionalId.get();
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, String.format("Эпик с данным id (id = %d) не найден", id));
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
                return;
            }

            if (exchange.getRequestMethod().equals("GET")) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, epic.toJson());
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
            }
            else if (exchange.getRequestMethod().equals("DELETE")) {
                taskManager.deleteEpicById(id);

                ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
            }
        }
    }

    private void postEpicsHandler(HttpExchange exchange) throws IOException {
        String json = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(""));

        Epic newEpic = null;
        ArrayList<Subtask> subtasks = new ArrayList<>();

        try {
            newEpic = Epic.fromJson(json);
            subtasks = Subtask.fromEpicJson(json);
        } catch (JsonParseException | TaskValidateException e) {
            ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, e.getMessage());
            ServerResponse.writeResponse(exchange, serverResponse.toJson(), 400);
            return;
        }

        if (newEpic.getId() != -1) {
            Epic foundEpic = taskManager.getEpicById(newEpic.getId());
            if (foundEpic != null) {
                taskManager.updateEpic(newEpic.getId(), newEpic);
            } else {
                newEpic.setId(-1);
                taskManager.addEpic(newEpic);


                Epic finalNewEpic1 = newEpic;
                subtasks.forEach(item -> taskManager.addSubtask(finalNewEpic1.getId(), item));
            }
        } else {
            taskManager.addEpic(newEpic);

            Epic finalNewEpic = newEpic;
            subtasks.forEach(item -> taskManager.addSubtask(finalNewEpic.getId(), item));
        }

        ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
        ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
    }
}
