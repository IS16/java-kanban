package main.java.com.practikum.kanban.KVServer;

import org.apiguardian.api.API;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String URL;
    private final String API_TOKEN;
    private final HttpClient client;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.URL = url;
        this.client = HttpClient.newHttpClient();
        this.API_TOKEN = register();
    }

    private String register() throws IOException, InterruptedException {
        URI url = URI.create(URL + "register");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(String.format("%ssave/%s?API_TOKEN=%s", URL, key, API_TOKEN));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String get(String key) throws IOException, InterruptedException {
        URI url = URI.create(String.format("%sload/%s?API_TOKEN=%s", URL, key, API_TOKEN));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
