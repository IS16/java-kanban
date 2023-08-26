package main.java.com.practikum.kanban.API.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.Exceptions.TaskValidateException;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.API.Enums.ServerResponseStatus;
import main.java.com.practikum.kanban.API.utils.Parser;
import main.java.com.practikum.kanban.API.utils.ServerResponse;

public class Tasks implements HttpHandler {
    private final TaskManager taskManager;
    public Tasks(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
            case "DELETE":
                getTasksHandler(exchange);
                return;
            case "POST":
                postTasksHandler(exchange);
                return;
            default:
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Такого эндпоинта не существует");
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
        }
    }

    private void getTasksHandler(HttpExchange exchange) throws IOException {
        HashMap<String, String> query = Parser.parseQuery(exchange.getRequestURI().getQuery());

        if (query.isEmpty() || !query.containsKey("id")) {
            if (exchange.getRequestMethod().equals("GET")) {
                ArrayList<Task> tasks = taskManager.getAllTasks();

                List<String> tasksJson = tasks.stream()
                        .map(Task::toJson)
                        .collect(Collectors.toList());

                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, tasksJson.toString());
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
            }
            else if (exchange.getRequestMethod().equals("DELETE")) {
                taskManager.deleteAllTasks();

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
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, String.format("Задача с данным id (id = %d) не найдена", id));
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
                return;
            }

            if (exchange.getRequestMethod().equals("GET")) {
                ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, task.toJson());
                ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
            }
            else if (exchange.getRequestMethod().equals("DELETE")) {
                taskManager.deleteTaskById(id);

                ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
                ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
            }
        }
    }

    private void postTasksHandler(HttpExchange exchange) throws IOException {
        String json = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(""));

        Task newTask = null;
        try {
            newTask = Task.fromJson(json);
        } catch (JsonParseException | TaskValidateException e) {
            ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, e.getMessage());
            ServerResponse.writeResponse(exchange, serverResponse.toJson(), 400);
            return;
        }

        if (newTask.getId() != -1) {
            Task findTask = taskManager.getTaskById(newTask.getId());
            if (findTask != null) {
                taskManager.updateTask(newTask.getId(), newTask);
            } else {
                newTask.setId(-1);
                taskManager.addTask(newTask);
            }
        } else {
            taskManager.addTask(newTask);
        }

        ServerResponse successResponse = new ServerResponse(ServerResponseStatus.OK, "");
        ServerResponse.writeResponse(exchange, successResponse.toJson(), 200);
    }
}
