package test.java.com.practikum.kanban;

import main.java.com.practikum.kanban.Managers.Managers;
import main.java.com.practikum.kanban.Managers.TaskManager.InMemoryTaskManager;
import main.java.com.practikum.kanban.Tasks.Epic;
import main.java.com.practikum.kanban.Tasks.Subtask;
import main.java.com.practikum.kanban.Tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Managers managers = new Managers();
    InMemoryTaskManager taskManager;
    Epic epic;

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    @BeforeEach
    public void prepareEnv() {
        taskManager = new InMemoryTaskManager(managers.getDefaultHistory());

        epic = new Epic("Тестовый эпик 1");
        taskManager.addEpic(epic);

        subtask1 = new Subtask("Первая подзадача", TaskStatus.NEW);
        subtask2 = new Subtask("Вторая подзадача", TaskStatus.NEW);
        subtask3 = new Subtask("Третья подзадача", TaskStatus.NEW);
    }

    @Test
    public void epicStatusShouldBeNewWithoutTasks() {
        assertEquals(epic.getStatus(), TaskStatus.NEW, "Неверный статус при создании пустого эпика");
    }

    @Test
    public void epicStatusShouldBeNewWithAllTasksWithNew() {
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        taskManager.addSubtask(epic.getId(), subtask3);

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Неверный статус при всех задачах со статусом NEW");
    }

    @Test
    public void epicStatusShouldBeInProgressWithTaskWithInProgress() {
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        taskManager.addSubtask(epic.getId(), subtask3);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Неверный статус при наличии задачи со статусом IN_PROGRESS");
    }

    @Test
    public void epicStatusShouldBeInProgressWithTasksWithNewAndDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        taskManager.addSubtask(epic.getId(), subtask3);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Неверный статус при наличии задач со статусами NEW и DONE");
    }

    @Test
    public void epicStatusShouldBeDoneWithAllTasksWithDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        taskManager.addSubtask(epic.getId(), subtask3);

        assertEquals(epic.getStatus(), TaskStatus.DONE, "Неверный статус при всех задачах со статусом DONE");
    }
}