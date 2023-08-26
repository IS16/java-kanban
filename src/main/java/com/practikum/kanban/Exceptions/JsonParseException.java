package main.java.com.practikum.kanban.Exceptions;

import java.text.ParseException;

public class JsonParseException extends ParseException {
    public JsonParseException(String message, int offset) {
        super(message, offset);
    }
}
