package main.java.com.practikum.kanban.utils;

public class Node<T> {
    public T data;
    public Node<T> prev;
    public Node<T> next;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
