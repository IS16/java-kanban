package com.practikum.kanban.Exceptions;

public class TaskValidateException extends RuntimeException {
    public TaskValidateException(String message) {
        super(message);
    }
}
