package com.practikum.kanban.Managers;

import com.practikum.kanban.Managers.HistoryManager.*;
import com.practikum.kanban.Managers.TaskManager.InMemoryTaskManager;
import com.practikum.kanban.Managers.TaskManager.TaskManager;

public class Managers {
    public TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
