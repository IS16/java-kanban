package com.practikum.kanban.Managers.HistoryManager;

import com.practikum.kanban.Tasks.Task;
import com.practikum.kanban.utils.CustomLinkedList;
import com.practikum.kanban.utils.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> nodesNav = new HashMap<>();
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (nodesNav.containsKey(task.getId())) {
            history.removeNode(nodesNav.get(task.getId()));
        }

        history.linkLast(task);
        nodesNav.put(task.getId(), history.tail);
    }

    @Override
    public void remove(int id) {
        if (nodesNav.containsKey(id)) {
            history.removeNode(nodesNav.get(id));
            nodesNav.remove(id);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public String toString() {
        return "History(" + history.getTasks() + ")";
    }
}
