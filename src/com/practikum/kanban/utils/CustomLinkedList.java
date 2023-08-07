package com.practikum.kanban.utils;

import java.util.ArrayList;

public class CustomLinkedList<T> {
    public Node<T> head;
    public Node<T> tail;

    public void linkLast(T element) {
        final Node<T> newTail = new Node<>(tail, element, null);
        if (head == null) {
            head = newTail;
        }

        if (tail != null) {
            tail.next = newTail;
        }

        tail = newTail;
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

            if (node.next != null) {
                node.next.prev = null;
            }
        } else if (node.next == null) {
            tail = node.prev;
            node.prev.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}