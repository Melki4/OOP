package ru.ssau.tk._repfor2lab_._OOP_.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.server.VaadinSession;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class BasicAuthClient {

    private static final String BASE_URL = "http://localhost:8080/projectFor3Lab";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String getCredentials() {
        String login = (String) VaadinSession.getCurrent().getAttribute("login");
        String password = (String) VaadinSession.getCurrent().getAttribute("password");
        if (login == null || password == null) return null;
        String credentials = login + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public static HttpRequest.Builder authorizedRequest(URI uri) {
        String auth = getCredentials();
        if (auth == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", auth)
                .timeout(Duration.ofSeconds(10));
    }

    public static HttpResponse<String> sendGet(String path) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(BASE_URL + path);
        HttpRequest request = authorizedRequest(uri).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendPost(String path, String jsonBody) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(BASE_URL + path);
        HttpRequest request = authorizedRequest(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendPut(String path, String jsonBody) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(BASE_URL + path);
        HttpRequest request = authorizedRequest(uri)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendDelete(String path) throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(BASE_URL + path);
        HttpRequest request = authorizedRequest(uri).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static String extractErrorMessage(String responseBody) {
        try {
            JsonNode node = mapper.readTree(responseBody);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {}
        return "Неизвестная ошибка сервера";
    }
}