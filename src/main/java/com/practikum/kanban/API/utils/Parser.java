package main.java.com.practikum.kanban.API.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import main.java.com.practikum.kanban.Exceptions.JsonParseException;

import java.util.*;

public class Parser {
    public static HashMap<String, String> parseQuery(String input) {
        HashMap<String, String> out = new HashMap<>();
        if (input == null) {
            return out;
        }

        ArrayList<String> query_array = new ArrayList<>(List.of(input.split("&")));
        query_array.forEach(query -> {
            String[] parts = query.split("=");
            out.put(parts[0], parts[1]);
        });

        return out;
    }

    public static Optional<Integer> parseId(HttpExchange exchange, String input) {
        try {
            return Optional.of(Integer.parseInt(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Map<String, JsonElement> parseJson(String json) throws JsonParseException {
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(json).getAsJsonObject();
        } catch (Exception e) {
            throw new JsonParseException("Невалидный JSON", 0);
        }

        return jsonObject.asMap();
    }
}
