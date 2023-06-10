import com.practikum.kanban.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        testTask(new TaskManager());
        testEpicSubtasks(new TaskManager());
        testSubtask(new TaskManager());
        testStatusUpdate(new TaskManager());
    }

    static void testTask(TaskManager taskManager) {
        Task task1 = new Task(taskManager.getCurId(),"Первая", "Моя первая задача", "NEW");
        Task task2 = new Task(taskManager.getCurId(), "Вторая", "NEW");

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка добавления задач и вывода всех существующих");
        System.out.println(taskManager.getAllTasks());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        System.out.println(taskManager.getAllTasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка получения задачи по ID");
        System.out.println(taskManager.getTaskById(2));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка обновления задачи");
        Task task3 = new Task(1, "Первая задача (Upd)", "Обновление первой задачи", "IN_PROGRESS");
        System.out.println(taskManager.getTaskById(1));
        taskManager.updateTask(task3);
        System.out.println(taskManager.getTaskById(1));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления задачи по ID");
        System.out.println(taskManager.getAllTasks());
        taskManager.deleteTaskById(1);
        System.out.println(taskManager.getAllTasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления всех задач");
        taskManager.addTask(task1);
        System.out.println(taskManager.getAllTasks());
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getAllTasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }

    static void testEpicSubtasks(TaskManager taskManager) {
        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "Моя самая первая подзадача", "NEW");
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", "NEW");
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        Epic epic1 = new Epic(taskManager.getCurId(), "Первый эпик", "Мой первый эпик", subtasks);
        Epic epic2 = new Epic(taskManager.getCurId(), "Второй эпик", new ArrayList<>());

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка добавления эпиков и вывода всех существующих");
        System.out.println(taskManager.getAllEpics());
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        System.out.println(taskManager.getAllEpics());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка получения эпика по ID");
        System.out.println(taskManager.getEpicById(4));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка обновления эпика");
        ArrayList<Subtask> subtasks1 = epic1.getSubtasks();
        subtasks1.remove(1);
        subtasks1.add(new Subtask(taskManager.getCurId(), "Третья подзадача", "NEW"));
        Epic epic3 = new Epic(3, "Первый эпик (Upd)", subtasks1);
        System.out.println(taskManager.getEpicById(3));
        taskManager.updateEpic(epic3);
        System.out.println(taskManager.getEpicById(3));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления эпика по ID");
        System.out.println(taskManager.getAllEpics());
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getAllEpics());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления всех эпиков");
        taskManager.addEpic(epic3);
        System.out.println(taskManager.getAllEpics());
        taskManager.deleteAllEpics();
        System.out.println(taskManager.getAllEpics());
        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }

    static void testSubtask(TaskManager taskManager) {
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "NEW");
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", "Тестовая подзадача", "NEW");

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка добавления подзадач");
        System.out.println(taskManager.getEpicById(1));
        taskManager.addSubtask(1, subtask1);
        taskManager.addSubtask(1, subtask2);
        System.out.println(taskManager.getEpicById(1));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        Epic epic2 = new Epic(taskManager.getCurId(), "Тестовый эпик 2", new ArrayList<>());
        taskManager.addEpic(epic2);
        Subtask subtask3 = new Subtask(taskManager.getCurId(), "Третья подзадача", "NEW");
        taskManager.addSubtask(epic2.getId(), subtask3);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка вывода всех подзадач");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка вывода всех подзадач конкретного эпика");
        System.out.println(taskManager.getAllSubtasksByEpicId(epic2.getId()));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка вывода конкретной подзадачи");
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        Subtask subtask4 = new Subtask(5, "Третья подзадача (Upd)", "IN_PROGRESS");

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка обновления подзадачи");
        System.out.println(taskManager.getSubtaskById(5));
        taskManager.updateSubtask(subtask4);
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления подзадачи");
        System.out.println(taskManager.getAllSubtasks());
        taskManager.deleteSubtaskById(5);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        taskManager.addSubtask(epic2.getId(), subtask3);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления всех подзадач конкретного эпика");
        System.out.println(taskManager.getAllSubtasks());
        taskManager.deleteAllSubtasksByEpicId(1);
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        taskManager.addSubtask(1, subtask1);
        taskManager.addSubtask(1, subtask2);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка удаления всех подзадач");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }

    static void testStatusUpdate(TaskManager taskManager) {
        Task task = new Task(taskManager.getCurId(), "Задача", "NEW");
        Task task1 = new Task(task.getId(), "Задача", "IN_PROGRESS");
        taskManager.addTask(task);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка обновления статуса задачи");
        System.out.println(taskManager.getTaskById(1));
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTaskById(1));
        System.out.println("--------------------------------------------------------------");
        System.out.println();

        ArrayList<Subtask> subtasks = new ArrayList<>();
        Subtask subtask1 = new Subtask(taskManager.getCurId(), "Первая подзадача", "NEW");
        Subtask subtask2 = new Subtask(taskManager.getCurId(), "Вторая подзадача", "Тестовая подзадача", "NEW");
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        Epic epic = new Epic(taskManager.getCurId(), "Тестовый эпик 1", new ArrayList<>());
        taskManager.addEpic(epic);

        System.out.println("--------------------------------------------------------------");
        System.out.println("Проверка обновления статуса подзадачи и эпика");
        System.out.println(taskManager.getEpicById(4));
        taskManager.addSubtask(4, subtask1);
        System.out.println(taskManager.getEpicById(4));
        Subtask subtask3 = new Subtask(subtask1.getId(), "Первая подзадача", "IN_PROGRESS");
        taskManager.addSubtask(4, subtask3);
        System.out.println(taskManager.getEpicById(4));
        Subtask subtask4 = new Subtask(subtask1.getId(), "Первая подзадача", "DONE");
        taskManager.addSubtask(4, subtask4);
        System.out.println(taskManager.getEpicById(4));
        taskManager.addSubtask(4, subtask2);
        System.out.println(taskManager.getEpicById(4));
        taskManager.deleteSubtaskById(3);
        System.out.println(taskManager.getEpicById(4));
        System.out.println("--------------------------------------------------------------");
        System.out.println();
    }
}