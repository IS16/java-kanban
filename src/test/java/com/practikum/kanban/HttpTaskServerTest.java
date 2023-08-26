package test.java.com.practikum.kanban;

import com.sun.net.httpserver.HttpServer;
import main.java.com.practikum.kanban.API.handlers.*;
import main.java.com.practikum.kanban.KVServer.KVServer;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.Tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private final int PORT = 8080;
    Managers managers = new Managers();
    HttpClient client = HttpClient.newHttpClient();
    KVServer server;
    HttpServer apiServer;
    TaskManager taskManager;

    @BeforeEach
    public void configParams() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();

        this.taskManager = managers.getDefault(managers.getDefaultHistory(), "http://localhost:8078/");
        apiServer = HttpServer.create();

        apiServer.bind(new InetSocketAddress(PORT), 0);
        apiServer.createContext("/tasks", new TasksList(taskManager));
        apiServer.createContext("/tasks/task", new Tasks(taskManager));
        apiServer.createContext("/tasks/subtask", new Subtasks(taskManager));
        apiServer.createContext("/tasks/epic", new Epics(taskManager));
        apiServer.createContext("/tasks/history", new History(taskManager));
        apiServer.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
        apiServer.stop(0);
    }

    private HttpResponse<String> getRequest(String uri) throws IOException, InterruptedException {
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> deleteRequest(String uri) throws IOException, InterruptedException {
        URI url = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> postRequest(String uri, String json) throws IOException, InterruptedException {
        URI url = URI.create(uri);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void tasksTaskGet() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Задача с данным id (id = 1) не найдена\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        res = getRequest("http://localhost:8080/tasks/task?id=sa");
        assertEquals(400, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Невалидный параметр \"id\"\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        Task task1 = new Task("Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);
        Task task2 = new Task("Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        res = getRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", task1.toJson(), task2.toJson()), res.body(), "Эндпоинт не вернул созданные задачи");

        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", task1.toJson()), res.body(), "Эндпоинт не вернул созданную задачу");
    }

    @Test
    public void tasksTaskDelete() throws IOException, InterruptedException {
        HttpResponse<String> res;

        Task task1 = new Task("Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);

        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", task1.toJson()), res.body(), "Эндпоинт не вернул созданную задачу");

        res = deleteRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Задача с данным id (id = 1) не найдена\"}", res.body(), "Эндпоинт вернул неверный ответ");

        taskManager.addTask(task1);
        Task task2 = new Task("Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        res = getRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", task1.toJson(), task2.toJson()), res.body(), "Эндпоинт не вернул созданные задачи");

        res = deleteRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул неверный ответ");
    }

    @Test
    public void tasksTaskPost() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/task");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        Task task = new Task("Первая задача");
        res = postRequest("http://localhost:8080/tasks/task", task.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        task.setId(1);
        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", task.toJson()), res.body(), "Эндпоинт не вернул созданную задачу");

        task.setStatus(TaskStatus.IN_PROGRESS);
        res = postRequest("http://localhost:8080/tasks/task", task.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/task?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", task.toJson()), res.body(), "Эндпоинт не вернул созданную задачу");
    }

    @Test
    public void tasksSubtaskGet() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        res = getRequest("http://localhost:8080/tasks/subtask?id=1");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Подзадача с данным id (id = 1) не найдена\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        res = getRequest("http://localhost:8080/tasks/subtask?id=sa");
        assertEquals(400, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Невалидный параметр \"id\"\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask("Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        res = getRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", subtask1.toJson(), subtask2.toJson()), res.body(), "Эндпоинт не вернул созданные подзадачи");

        res = getRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", subtask1.toJson()), res.body(), "Эндпоинт не вернул созданную подзадачу");
    }

    @Test
    public void tasksSubtaskDelete() throws IOException, InterruptedException {
        HttpResponse<String> res;

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask("Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);

        res = getRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", subtask1.toJson()), res.body(), "Эндпоинт не вернул созданную подзадачу");

        res = deleteRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Подзадача с данным id (id = 2) не найдена\"}", res.body(), "Эндпоинт вернул неверный ответ");

        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        res = getRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", subtask1.toJson(), subtask2.toJson()), res.body(), "Эндпоинт не вернул созданные подзадачи");

        res = deleteRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул неверный ответ");
    }

    @Test
    public void tasksSubtaskPost() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/subtask");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        subtask1.setEpicId(epic1.getId());

        res = postRequest("http://localhost:8080/tasks/subtask", subtask1.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        subtask1.setId(2);
        res = getRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", subtask1.toJson()), res.body(), "Эндпоинт не вернул созданную подзадачу");

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        res = postRequest("http://localhost:8080/tasks/subtask", subtask1.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/subtask?id=2");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", subtask1.toJson()), res.body(), "Эндпоинт не вернул созданную подзадачу");
    }

    @Test
    public void tasksEpicGet() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Эпик с данным id (id = 1) не найден\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        res = getRequest("http://localhost:8080/tasks/epic?id=sa");
        assertEquals(400, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Невалидный параметр \"id\"\"}", res.body(), "Эндпоинт вернул неверную ошибку");

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Второй эпик");
        taskManager.addEpic(epic2);

        res = getRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", epic1.toJson(), epic2.toJson()), res.body(), "Эндпоинт не вернул созданные эпики");

        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", epic1.toJson()), res.body(), "Эндпоинт не вернул созданный эпик");
    }

    @Test
    public void tasksEpicDelete() throws IOException, InterruptedException {
        HttpResponse<String> res;

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", epic1.toJson()), res.body(), "Эндпоинт не вернул созданный эпик");

        res = deleteRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(404, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"ERROR\",\"errorMessage\":\"Эпик с данным id (id = 1) не найден\"}", res.body(), "Эндпоинт вернул неверный ответ");

        taskManager.addEpic(epic1);
        Epic epic2 = new Epic("Второй эпик");
        taskManager.addEpic(epic2);

        res = getRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s]}", epic1.toJson(), epic2.toJson()), res.body(), "Эндпоинт не вернул созданные эпики");

        res = deleteRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул неверный ответ");
    }

    @Test
    public void tasksEpicPost() throws IOException, InterruptedException {
        HttpResponse<String> res;

        res = getRequest("http://localhost:8080/tasks/epic");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул не пустой ответ при запуске");

        Epic epic1 = new Epic("Первый эпик");

        res = postRequest("http://localhost:8080/tasks/epic", epic1.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        epic1.setId(1);
        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", epic1.toJson()), res.body(), "Эндпоинт не вернул созданный эпик");

        epic1.setDescription("Обновление");
        res = postRequest("http://localhost:8080/tasks/epic", epic1.toJson());
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\"}", res.body(), "Эндпоинт вернул неверный ответ");

        res = getRequest("http://localhost:8080/tasks/epic?id=1");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", epic1.toJson()), res.body(), "Эндпоинт не вернул созданный эпик");
    }

    @Test
    public void tasksGet() throws IOException, InterruptedException {
        HttpResponse<String> res;

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask("Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        Epic epic2 = new Epic("Второй эпик");
        taskManager.addEpic(epic2);

        Task task1 = new Task("Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);
        Task task2 = new Task("Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        ArrayList<Task> tasks = taskManager.getPrioritizedTasks();

        List<String> tasksJson = tasks.stream()
                .map(Task::toJson)
                .collect(Collectors.toList());

        res = getRequest("http://localhost:8080/tasks");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":%s}", tasksJson), res.body(), "Эндпоинт не вернул созданные задачи");

        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();

        res = getRequest("http://localhost:8080/tasks");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул задачи");
    }

    @Test
    public void historyGet() throws IOException, InterruptedException {
        HttpResponse<String> res;

        Task task1 = new Task("Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);
        Task task2 = new Task("Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);
        Task task3 = new Task("Третья", "", TaskStatus.DONE);
        taskManager.addTask(task3);

        res = getRequest("http://localhost:8080/tasks/history");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals("{\"status\":\"OK\",\"response\":[]}", res.body(), "Эндпоинт вернул задачи");

        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);

        res = getRequest("http://localhost:8080/tasks/history");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s, %s]}", task1.toJson(), task3.toJson(), task2.toJson()), res.body(), "Эндпоинт не вернул задачи");

        taskManager.getTaskById(3);

        res = getRequest("http://localhost:8080/tasks/history");
        assertEquals(200, res.statusCode(), "Эндпоинт вернул неверный код ответа");
        assertEquals(String.format("{\"status\":\"OK\",\"response\":[%s, %s, %s]}", task1.toJson(), task2.toJson(), task3.toJson()), res.body(), "Эндпоинт не вернул задачи");
    }
}
