package com.practikum.kanban.Managers.HistoryManager;

import com.practikum.kanban.Tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void add(Task task);
    public ArrayList<Task> getHistory();
}
