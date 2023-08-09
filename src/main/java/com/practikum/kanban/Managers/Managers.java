package main.java.com.practikum.kanban.Managers;

import main.java.com.practikum.kanban.Managers.HistoryManager.InMemoryHistoryManager;
import main.java.com.practikum.kanban.Managers.TaskManager.InMemoryTaskManager;
import main.java.com.practikum.kanban.Managers.TaskManager.TaskManager;
import main.java.com.practikum.kanban.Managers.HistoryManager.HistoryManager;

public class Managers {
    public TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
