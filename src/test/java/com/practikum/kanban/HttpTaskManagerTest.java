package test.java.com.practikum.kanban;

import main.java.com.practikum.kanban.Exceptions.JsonParseException;
import main.java.com.practikum.kanban.KVServer.KVServer;
import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Managers.TaskManager.FileBackedTasksManager;
import main.java.com.practikum.kanban.Managers.TaskManager.HttpTaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.Task;
import main.java.com.practikum.kanban.Tasks.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    Managers managers = new Managers();
    KVServer server;

    @BeforeEach
    public void configParams() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();

        super.taskManager = new HttpTaskManager("http://localhost:8078/", managers.getDefaultHistory());
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void addNewTask() {
        super.addNewTask();
    }

    @Test
    public void shouldReturnNullOnInvalidTaskId() {
        super.shouldReturnNullOnInvalidTaskId();
    }

    @Test
    public void shouldReturnEmptyArrayWithoutAddingTasks() {
        super.shouldReturnEmptyArrayWithoutAddingTasks();
    }

    @Test
    public void updateTasks() {
        super.updateTasks();
    }

    @Test
    public void deleteTasks() {
        super.deleteTasks();
    }

    @Test
    public void addNewEpic() {
        super.addNewEpic();
    }

    @Test
    public void shouldReturnNullOnInvalidEpicId() {
        super.shouldReturnNullOnInvalidEpicId();
    }

    @Test
    public void shouldReturnEmptyArrayWithoutAddingEpics() {
        super.shouldReturnEmptyArrayWithoutAddingEpics();
    }

    @Test
    public void updateEpics() {
        super.updateEpics();
    }

    @Test
    public void deleteEpics() {
        super.deleteEpics();
    }

    @Test
    public void addNewSubtask() {
        super.addNewSubtask();
    }

    @Test
    public void shouldReturnNullOnInvalidSubtaskId() {
        super.shouldReturnNullOnInvalidSubtaskId();
    }

    @Test
    public void shouldReturnEmptyArrayWithoutAddingSubtasks() {
        super.shouldReturnEmptyArrayWithoutAddingSubtasks();
    }

    @Test
    public void subtaskAndEpicShouldKnowAboutEachOther() {
        super.subtaskAndEpicShouldKnowAboutEachOther();
    }

    @Test
    public void updateSubtasks() {
        super.updateSubtasks();
    }

    @Test
    public void deleteSubtasks() {
        super.deleteSubtasks();
    }

    @Test
    public void getHistory() {
        super.getHistory();
    }

    @Test
    public void taskWithStartTimeAndDurationTest() {
        super.taskWithStartTimeAndDurationTest();
    }

    @Test
    public void epicWithStartTimeAndDurationTest() {
        super.epicWithStartTimeAndDurationTest();
    }

    @Test
    public void prioritizedTasksTest() {
        super.prioritizedTasksTest();
    }

    @Test
    public void saveHistoryToDbTest() throws IOException, JsonParseException, InterruptedException {
        Task taskToAdd = new Task("Первая задача", "", TaskStatus.NEW);
        taskManager.addTask(taskToAdd);

        HttpTaskManager taskManager1 = HttpTaskManager.loadFromDB("http://localhost:8078/");
        final Task task = taskManager1.getTaskById(taskToAdd.getId());
        assertNotNull(task, "Задача не импортирована");

        taskManager.deleteAllTasks();
        taskManager1 = HttpTaskManager.loadFromDB("http://localhost:8078/");
        assertEquals(0, taskManager1.getAllTasks().size(), "Появились лишние задачи");
        assertEquals(0, taskManager1.getHistory().size(), "История не пустая");

        Epic epic1 = new Epic("Первый эпик");
        taskManager.addEpic(epic1);

        taskManager1 = HttpTaskManager.loadFromDB("http://localhost:8078/");
        assertEquals(1, taskManager1.getAllEpics().size(), "Эпик не импортирован");
        assertEquals(0, taskManager1.getAllSubtasks().size(), "Появились лишние подзадачи");

        Subtask subtask1 = new Subtask("Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.getEpicById(epic1.getId());

        taskManager1 = HttpTaskManager.loadFromDB("http://localhost:8078/");
        assertEquals(1, taskManager1.getAllSubtasks().size(), "Подзадачи не добавились");
        assertEquals(1, taskManager1.getHistory().size(), "История не импортировалась");
    }
}