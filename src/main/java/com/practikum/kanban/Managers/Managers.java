package main.java.com.practikum.kanban.Managers;

import main.java.com.practikum.kanban.Managers.HistoryManager.InMemoryHistoryManager;
import main.java.com.practikum.kanban.Managers.TaskManager.HttpTaskManager;
import main.java.com.practikum.kanban.Managers.TaskManager.InMemoryTaskManager;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Managers.HistoryManager.HistoryManager;

import java.io.IOException;

public class Managers {
    public TaskManager getDefault(HistoryManager historyManager, String db) throws IOException, InterruptedException {
        return new HttpTaskManager(db, historyManager);
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
