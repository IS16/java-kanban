package main.java.com.practikum.kanban.API.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.API.Enums.ServerResponseStatus;
import main.java.com.practikum.kanban.API.utils.ServerResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TasksList implements HttpHandler {
    private final TaskManager taskManager;

    public TasksList(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            getTasksHandler(exchange);
        } else {
            ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.ERROR, "Такого эндпоинта не существует");
            ServerResponse.writeResponse(exchange, serverResponse.toJson(), 404);
        }
    }

    private void getTasksHandler(HttpExchange exchange) throws IOException {
        ArrayList<Task> tasks = taskManager.getPrioritizedTasks();

        List<String> historyJson = tasks.stream()
                .map(Task::toJson)
                .collect(Collectors.toList());

        ServerResponse serverResponse = new ServerResponse(ServerResponseStatus.OK, historyJson.toString());
        ServerResponse.writeResponse(exchange, serverResponse.toJson(), 200);
    }
}
