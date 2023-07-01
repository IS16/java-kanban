package com.practikum.kanban.Managers.HistoryManager;

import com.practikum.kanban.Tasks.Subtask;
import com.practikum.kanban.Tasks.Task;
import com.practikum.kanban.utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class CustomLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;
    private int size = 0;

    public void linkLast(T element) {
        final Node<T> oldTail = tail;
        final Node<T> newTail = new Node<>(oldTail, element, null);
        tail = newTail;
        if (size == 0) {
            head = newTail;
        }

        if (oldTail != null) {
            oldTail.next = newTail;
        }

        size++;
    }

    public ArrayList<T> getTasks() {
        ArrayList<T> out = new ArrayList<>();
        Node<T> curNode = head;

        while (curNode != null) {
            out.add(curNode.data);
            curNode = curNode.next;
        }

        return out;
    }

    public void removeNode(Node<T> node) {
        if (node.prev == null) {
            head = node.next;
            node.next.prev = null;
        } else if (node.next == null) {
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        size--;
    }
}

public class InMemoryHistoryManager implements HistoryManager {    private final HashMap<Integer, Node<Task>> nodesNav = new HashMap<>();
    private final CustomLinkedList<Task> history = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (!nodesNav.containsKey(task.getId())) {
            history.linkLast(task);
        } else {
            history.removeNode(nodesNav.get(task.getId()));
            history.linkLast(task);
        }

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
