package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;
import tasks.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TaskHandler extends Handler {
    static final String PATH_NAME = "tasks";

    TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange, PATH_NAME);
        handleExecute(exchange, endpoint);
    }

    protected void handleExecute(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint) {
            case READ_ALL:
                handleReadAll(exchange);
                break;
            case READ_ONE:
                handleReadOne(exchange);
                break;
            case CREATE:
                handleCreate(exchange);
                break;
            case UPDATE:
                handleUpdate(exchange);
                break;
            case DELETE:
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, 500);
        }
    }

    protected Endpoint getEndpoint(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = getRequestPathParts(exchange);

        if (pathParts.length < 2 || !path.equals(pathParts[1])) {
            return Endpoint.UNKNOWN;
        }

        String requestMethod = exchange.getRequestMethod();
        if (pathParts.length == 2) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.READ_ALL;
                case "POST":
                    try {
                        JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
                        return jsonObject.has("id") ? Endpoint.UPDATE : Endpoint.CREATE;
                    } catch (Exception e) {
                        return Endpoint.UNKNOWN;
                    }
            }
        } else if (pathParts.length == 3) {
            switch (requestMethod) {
                case "GET":
                    return Endpoint.READ_ONE;
                case "DELETE":
                    return Endpoint.DELETE;
            }
        }
        return Endpoint.UNKNOWN;
    }

    void handleReadAll(HttpExchange exchange) throws IOException {
        String body = gson.toJson(taskManager.getTasks().values(), List.class);
        sendResponse(exchange, body, 200);
    }

    void handleReadOne(HttpExchange exchange) throws IOException {
        try {
            int taskId = parseTaskIdFromUri(exchange);
            if (taskManager.getTasks().containsKey(taskId)) {
                String body = gson.toJson(taskManager.getTask(taskId));
                sendResponse(exchange, body, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    void handleCreate(HttpExchange exchange) throws IOException {
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            Task task = Task.cloneWithNextId(gson.fromJson(jsonObject, Task.class));

            if (taskManager.isIntersectedTask(task)) {
                sendResponse(exchange, 406);
            } else {
                taskManager.addTask(task);
                sendResponse(exchange, 201);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            Task task = gson.fromJson(jsonObject, Task.class);

            if (taskManager.getTasks().containsKey(task.getId())) {
                if (taskManager.isIntersectedTask(task)) {
                    sendResponse(exchange, 406);
                } else {
                    taskManager.updateTask(task);
                    sendResponse(exchange, 200);
                }
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    void handleDelete(HttpExchange exchange) throws IOException {
        try {
            int taskId = parseTaskIdFromUri(exchange);
            if (taskManager.getTasks().containsKey(taskId)) {
                taskManager.removeTask(taskId);
                sendResponse(exchange, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    protected int parseTaskIdFromUri(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");
        return Integer.parseInt(pathParts[2]);
    }

    protected JsonObject getJsonObjectFromRequestBody(HttpExchange exchange) throws IOException {
        String body = getRequestBody(exchange);
        JsonElement jsonElement = JsonParser.parseString(body);
        return jsonElement.getAsJsonObject();
    }

    protected String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes());
        InputStream requestBodyStream = new ByteArrayInputStream(body.getBytes());
        exchange.setStreams(requestBodyStream, null);
        return body;
    }

    protected enum Endpoint {
        READ_ALL,
        READ_ONE,
        CREATE,
        UPDATE,
        DELETE,
        UNKNOWN
    }
}
