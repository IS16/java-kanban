package com.practikum.kanban.Managers.TaskManager;

import com.practikum.kanban.Managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    Managers managers = new Managers();

    @BeforeEach
    public void createTaskManager() {
        super.taskManager = new InMemoryTaskManager(managers.getDefaultHistory());
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
}