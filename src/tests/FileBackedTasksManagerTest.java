package tests;

import com.practikum.kanban.Managers.Managers;
import com.practikum.kanban.Managers.TaskManager.FileBackedTasksManager;
import com.practikum.kanban.Tasks.Epic;
import com.practikum.kanban.Tasks.Subtask;
import com.practikum.kanban.Tasks.Task;
import com.practikum.kanban.Tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    Managers managers = new Managers();

    @BeforeEach
    public void createTaskManager() {
        super.taskManager = new FileBackedTasksManager(managers.getDefaultHistory(), "data.save");
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
    public void saveHistoryToFileTest() {
        Task taskToAdd = new Task(taskManager.getCurId(), "Первая задача", "", TaskStatus.NEW);
        taskManager.addTask(taskToAdd);

        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile("data.save");
        final Task task = taskManager1.getTaskById(taskToAdd.getId());
        assertNotNull(task, "Задача не импортирована");

        taskManager.deleteAllTasks();
        taskManager1 = FileBackedTasksManager.loadFromFile("data.save");
        assertEquals(0, taskManager1.getAllTasks().size(), "Появились лишние задачи");
        assertEquals(0, taskManager1.getHistory().size(), "История не пустая");

        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", new ArrayList<>());
        taskManager.addEpic(epic1);

        taskManager1 = FileBackedTasksManager.loadFromFile("data.save");
        assertEquals(1, taskManager1.getAllEpics().size(), "Эпик не импортирован");
        assertEquals(0, taskManager1.getAllSubtasks().size(), "Появились лишние подзадачи");

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.getEpicById(epic1.getId());

        taskManager1 = FileBackedTasksManager.loadFromFile("data.save");
        assertEquals(1, taskManager1.getAllSubtasks().size(), "Подзадачи не добавились");
        assertEquals(1, taskManager1.getHistory().size(), "История не импортировалась");
    }

    @Test
    public void prioritizedTasksTest() {
        super.prioritizedTasksTest();
    }
}