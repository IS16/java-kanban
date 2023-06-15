package com.practikum.kanban;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_MAX_SIZE = 10;
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() == HISTORY_MAX_SIZE) {
            history.remove(0);
        }

        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "History(" + history + ")";
    }
}
