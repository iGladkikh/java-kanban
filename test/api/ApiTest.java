package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import services.Managers;
import services.TaskManager;
import tasks.Status;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class ApiTest {
    public static final String HOST = "http://localhost:8080";
    static TaskManager manager;
    static HttpTaskServer server;
    static HttpClient client;
    static Gson gson;

    @BeforeAll
    static void setUp() {
        manager = Managers.getDefaultTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new Handler.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new Handler.DurationAdapter())
                .registerTypeAdapter(Status.class, new Handler.StatusAdapter())
                .create();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
        client.close();
    }

    public HttpResponse<String> sendGetRequest(String uri) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendPostRequest(String uri, String body) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .headers("Content-type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpResponse<String> sendDeleteRequest(String uri) {
        try {
            URI url = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}