package main.java.com.practikum.kanban.API.utils;

import com.sun.net.httpserver.HttpExchange;
import main.java.com.practikum.kanban.API.Enums.ServerResponseStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ServerResponse {
    private final ServerResponseStatus status;
    private final String response;

    public ServerResponse(ServerResponseStatus status, String response) {
        this.status = status;
        this.response = response;
    }

    public String toJson() {
        if (status == ServerResponseStatus.ERROR) {
            return String.format("{\"status\":\"%s\",\"errorMessage\":\"%s\"}", status, response);
        } else {
            if (!response.isEmpty()) {
                return String.format("{\"status\":\"%s\",\"response\":%s}", status, response);
            } else {
                return String.format("{\"status\":\"%s\"}", status);
            }
        }
    }

    public static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        if(responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}
