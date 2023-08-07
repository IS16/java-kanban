package com.practikum.kanban.Tasks;

import com.practikum.kanban.Managers.Managers;
import com.practikum.kanban.Managers.TaskManager.InMemoryTaskManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Managers managers = new Managers();
    InMemoryTaskManager taskManager = new InMemoryTaskManager(managers.getDefaultHistory());

    @Test
    public void epicStatusUpdatingTest() {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Неверный статус при создании пустого эпика");

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(taskManager.getCurId(), "Третья подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);
        taskManager.addSubtask(epic.getId(), subtask3);

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Неверный статус при всех задачах со статусом NEW");

        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Неверный статус при наличии задачи со статусом IN_PROGRESS");

        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Неверный статус при наличии задач со статусами NEW и DONE");

        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        assertEquals(epic.getStatus(), TaskStatus.DONE, "Неверный статус при всех задачах со статусом DONE");
    }
}