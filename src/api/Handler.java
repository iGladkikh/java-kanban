package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.TaskManager;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class Handler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Status.class, new StatusAdapter())
            .create();
    protected final TaskManager taskManager;

    Handler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected String[] getRequestPathParts(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        return requestPath.split("/", -1);
    }

    protected void sendResponse(HttpExchange exchange,
                                String responseString,
                                int responseCode) throws IOException {
        try (OutputStream out = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, 0);
            out.write(responseString.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void sendResponse(HttpExchange exchange, int responseCode) throws IOException {
        sendResponse(exchange, "", responseCode);
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(Task.DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String time = jsonReader.nextString();
            if (time.isBlank()) {
                return null;
            }
            return LocalDateTime.parse(time, Task.DATE_TIME_FORMATTER);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            String duration = jsonReader.nextString();
            if (duration.isBlank()) {
                return null;
            }
            return Duration.ofMinutes(Long.parseLong(duration));
        }
    }

    static class StatusAdapter extends TypeAdapter<Status> {
        @Override
        public void write(JsonWriter jsonWriter, Status status) throws IOException {
            if (status == null) {
                jsonWriter.value(Status.NEW.toString());
                return;
            }
            jsonWriter.value(status.toString());
        }

        @Override
        public Status read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return Status.NEW;
            }
            String status = jsonReader.nextString();
            if (status.isBlank()) {
                return Status.NEW;
            }
            return Status.valueOf(status);
        }
    }
}
