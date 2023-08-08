package tests;

import com.practikum.kanban.Exceptions.TaskValidateException;
import com.practikum.kanban.Managers.TaskManager.TaskManager;
import com.practikum.kanban.Tasks.Epic;
import com.practikum.kanban.Tasks.Subtask;
import com.practikum.kanban.Tasks.Task;
import com.practikum.kanban.Tasks.TaskStatus;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    public void addNewTask() {
        Task taskToAdd = new Task(taskManager.getCurId(), "Первая задача", "", TaskStatus.NEW);
        taskManager.addTask(taskToAdd);

        final Task task = taskManager.getTaskById(taskToAdd.getId());
        assertNotNull(task);
        assertEquals(taskToAdd, task, "Задачи не совпадают");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают");
    }

    public void shouldReturnNullOnInvalidTaskId() {
        final Task taskWithInvalidId = taskManager.getTaskById(-1);
        assertNull(taskWithInvalidId, "Вернулась какая-то задача с неверным taskId");
    }

    public void shouldReturnEmptyArrayWithoutAddingTasks() {
        final ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Список не пустой");
    }

    public void updateTasks() {
        Task taskToAdd = new Task(taskManager.getCurId(), "Задача для обновления", "", TaskStatus.NEW);
        taskManager.addTask(taskToAdd);

        Task taskToUpdate = new Task(taskToAdd.getId(), "Задача обновилась", "", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(taskToUpdate);

        final Task task = taskManager.getTaskById(taskToUpdate.getId());
        assertEquals(taskToAdd.getId(), task.getId(), "Разные задачи");
        assertNotEquals(taskToAdd, taskToUpdate, "Задачи совпадают");
        assertEquals(task.getTitle(), "Задача обновилась", "Название не изменилось");
        assertEquals(task.getStatus(), TaskStatus.IN_PROGRESS, "Статус не изменился");
    }

    public void deleteTasks() {
        Task taskToAdd = new Task(taskManager.getCurId(), "Задача для удаления", "", TaskStatus.NEW);
        Task taskToAdd1 = new Task(taskManager.getCurId(), "Задача для удаления № 2", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(taskToAdd);

        assertEquals(1, taskManager.getAllTasks().size(), "Задача не добавилась");

        taskManager.deleteTaskById(taskToAdd.getId());
        assertEquals(0, taskManager.getAllTasks().size(), "Задача не удалилась");

        taskManager.addTask(taskToAdd);
        taskManager.addTask(taskToAdd1);
        assertEquals(2, taskManager.getAllTasks().size(), "Задачи не добавились");

        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size(), "Задачи не удалились");
    }

    public void addNewEpic() {
        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", "Мой первый эпик", subtasks);
        taskManager.addEpic(epic1);

        final Epic epic = taskManager.getEpicById(epic1.getId());
        assertNotNull(epic);
        assertEquals(epic1, epic, "Эпики не совпадают");

        final ArrayList<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");

        final ArrayList<Subtask> subtasksList = taskManager.getAllSubtasks();
        assertEquals(2, subtasksList.size(), "Подзадачи не добавлены");

        assertEquals(subtask1.getEpicId(), epic1.getId(), "Подзадача не знает о своём эпике");
        assertTrue(epic.hasSubtask(subtask1.getId()), "Эпик не знает о подзадаче");
    }

    public void shouldReturnNullOnInvalidEpicId() {
        final Task epicWithInvalidId = taskManager.getEpicById(-1);
        assertNull(epicWithInvalidId, "Вернулся какой-то эпик с неверным epicId");
    }

    public void shouldReturnEmptyArrayWithoutAddingEpics() {
        final ArrayList<Epic> epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "Список не пустой");
    }

    public void updateEpics() {
        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", "Мой первый эпик", subtasks);
        taskManager.addEpic(epic1);

        Epic epic3 = new Epic(epic1.getId(), "Первый эпик (Upd)", "Тест", epic1.getSubtasks());
        taskManager.updateEpic(epic3);

        final Epic epic = taskManager.getEpicById(epic1.getId());
        assertNotEquals(epic1, epic, "Эпики совпадают");

        assertNotEquals(epic.getTitle(), epic1.getTitle(), "Названия эпиков совпадают");
        assertNotEquals(epic.getDescription(), epic1.getDescription(), "Описания эпиков совпадают");
        assertEquals(epic.getSubtasks(), epic1.getSubtasks(), "Подзадачи не совпадают");
    }

    public void deleteEpics() {
        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", "Мой первый эпик", subtasks);
        taskManager.addEpic(epic1);
        Epic epic2 = new Epic(taskManager.getCurId(), "Второй эпик", new ArrayList<>());
        taskManager.addEpic(epic2);

        assertEquals(2, taskManager.getAllEpics().size(), "Эпики не добавились");

        assertEquals(2, taskManager.getAllSubtasks().size(), "Подзадачи не добавились");
        taskManager.deleteEpicById(epic1.getId());
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не удалился");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалились");

        taskManager.addEpic(epic1);

        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size(), "Эпики не удалились");
    }

    public void addNewSubtask() {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic.getId(), subtask1);

        final Subtask subtask = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(subtask, "Подзадача не найдена");

        assertEquals(subtask, subtask1, "Подзадачи не совпадают");

        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Неверное количество Подзадач");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    public void shouldReturnNullOnInvalidSubtaskId() {
        final Task subtaskWithInvalidId = taskManager.getSubtaskById(-1);
        assertNull(subtaskWithInvalidId, "Вернулась какая-то подзадача с неверным subtaskId");
    }

    public void shouldReturnEmptyArrayWithoutAddingSubtasks() {
        final ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(0, subtasks.size(), "Список не пустой");
    }

    public void subtaskAndEpicShouldKnowAboutEachOther() {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic.getId(), subtask1);

        assertEquals(subtask1.getEpicId(), epic.getId(), "Подзадача не знает о своём эпике");
        assertTrue(epic.hasSubtask(subtask1.getId()), "Эпик не знает о своей подзадаче");
    }

    public void updateSubtasks() {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic.getId(), subtask1);

        Subtask subtask2 = new Subtask(subtask1.getId(), subtask1.getEpicId(),"Третья подзадача (Upd)", TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        final Subtask subtask = taskManager.getSubtaskById(subtask2.getId());
        assertNotNull(subtask, "Подзадача не возвращается");
        assertEquals(subtask1.getId(), subtask.getId(), "subtaskId не совпадают");
        assertNotEquals(subtask.getTitle(), subtask1.getTitle(), "Название подзадачи не обновлилось");
        assertNotEquals(subtask.getStatus(), subtask1.getStatus(), "Статус подзадачи не обновился");
    }

    public void deleteSubtasks() {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", "Тестовая подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic.getId(), subtask1);
        taskManager.addSubtask(epic.getId(), subtask2);

        Epic epic2 = new Epic(taskManager.getCurId(), "Тестовый эпик 2", new ArrayList<>());
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask(taskManager.getCurId(), "Третья подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic2.getId(), subtask3);

        assertEquals(3, taskManager.getAllSubtasks().size(), "Подзадачи не добавились");

        assertEquals(2, taskManager.getAllSubtasksByEpicId(epic.getId()).size(), "Подзадачи не добавились в эпик");

        taskManager.deleteSubtaskById(subtask1.getId());
        assertEquals(1, taskManager.getAllSubtasksByEpicId(epic.getId()).size(), "Подзадача не удалилась");

        taskManager.addSubtask(subtask1.getEpicId(), subtask1);
        taskManager.deleteAllSubtasksByEpicId(subtask1.getEpicId());
        assertEquals(0, taskManager.getAllSubtasksByEpicId(epic.getId()).size(), "Подзадачи не удалились из эпика");

        assertNotEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не найдены");

        taskManager.deleteAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не удалились");
    }

    public void getHistory() {
        Task taskToAdd = new Task(taskManager.getCurId(), "Первая задача", "", TaskStatus.NEW);
        taskManager.addTask(taskToAdd);

        Task taskToAdd1 = new Task(taskManager.getCurId(), "Вторая задача", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(taskToAdd1);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", "Мой первый эпик", subtasks);
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic(taskManager.getCurId(), "Второй эпик", new ArrayList<>());
        taskManager.addEpic(epic2);

        assertNotNull(taskManager.getHistory(), "История не возвращается");
        assertEquals(0, taskManager.getHistory().size(), "В начале история не пустая");

        taskManager.getTaskById(taskToAdd.getId());
        taskManager.getTaskById(taskToAdd1.getId());
        taskManager.getTaskById(taskToAdd.getId());

        assertEquals(2, taskManager.getHistory().size(), "Происходит дублирование задач");
        assertEquals(taskToAdd1, taskManager.getHistory().get(0), "Задачи не совпадают");

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());

        assertEquals(4, taskManager.getHistory().size(), "Эпики не записались в просмотры");

        taskManager.deleteTaskById(taskToAdd1.getId());
        assertEquals(3, taskManager.getHistory().size(), "Задача не удалилась из начала истории просмотров");
        assertFalse(taskManager.getHistory().contains(taskToAdd1), "Задача не удалилась");

        Subtask subtask3 = new Subtask(taskManager.getCurId(), "Третья подзадача", TaskStatus.NEW);
        taskManager.addSubtask(epic1.getId(), subtask3);

        taskManager.getSubtaskById(subtask3.getId());

        assertEquals(4, taskManager.getHistory().size(), "Подзадача не добавилась в историю просмотров");

        taskManager.getTaskById(taskToAdd.getId());
        assertEquals(4, taskManager.getHistory().size(), "Дублирование задач");
        assertEquals(taskToAdd, taskManager.getHistory().get(taskManager.getHistory().size() - 1), "Просмотр не переместился в конец");

        taskManager.deleteTaskById(taskToAdd.getId());
        assertFalse(taskManager.getHistory().contains(taskToAdd), "Задача не удалилась из конца истории просмотров");

        taskManager.deleteEpicById(epic1.getId());
        assertFalse(taskManager.getHistory().contains(epic1), "Эпик не удалился из середины истории просмотров");
    }

    public void taskWithStartTimeAndDurationTest() {
        Task task1 = new Task(taskManager.getCurId(), "Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);

        final Task task = taskManager.getTaskById(task1.getId());
        assertEquals(LocalDateTime.of(2023, 8, 7, 12, 30, 0), task.getEndTime(), "Неверное вычисление даты окончания");
    }

    public void epicWithStartTimeAndDurationTest() {
        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", new ArrayList<>());
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        final Epic epic = taskManager.getEpicById(epic1.getId());
        assertEquals(LocalDateTime.of(2023, 9, 1, 11, 0, 0), epic.getStartTime(), "Неверно определена дата начала");
        assertEquals(160, epic.getDuration(), "Неверно определена длительность эпика");
        assertEquals(LocalDateTime.of(2023, 9, 2, 15, 40, 0), epic.getEndTime(), "Неверно определена дата окончания");
    }

    public void prioritizedTasksTest() {
        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", new ArrayList<>());
        taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 0, 0), 120);
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 2, 15, 0, 0), 40);
        taskManager.addSubtask(epic1.getId(), subtask1);
        taskManager.addSubtask(epic1.getId(), subtask2);

        Task task1 = new Task(taskManager.getCurId(), "Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 8, 7,12, 0, 0), 30);
        taskManager.addTask(task1);
        Task task2 = new Task(taskManager.getCurId(), "Вторая", "", TaskStatus.IN_PROGRESS);
        taskManager.addTask(task2);

        final ArrayList<Task> tasks = taskManager.getPrioritizedTasks();
        assertEquals(tasks.get(0), task1, "Самая ранняя задача не самая первая");
        assertEquals(tasks.get(tasks.size() - 1), task2, "Задача без даты не последняя");

        taskManager.deleteTaskById(task1.getId());
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Задача не удалилась");

        Task task3 = new Task(taskManager.getCurId(), "Первая", "Моя первая задача", TaskStatus.NEW, LocalDateTime.of(2023, 9, 1, 11, 10, 0), 30);
        final TaskValidateException exception = assertThrows(
                TaskValidateException.class,
                new Executable() {
                    @Override
                    public void execute() throws Throwable {
                        taskManager.addTask(task3);
                    }
                }
        );

        assertEquals(exception.getMessage(), String.format("Задача пересекается с другой задачей (ID: %d)", subtask1.getId()));
    }
}