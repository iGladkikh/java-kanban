package api;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends TaskHandler {
    static final String PATH_NAME = "epics";

    EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange, PATH_NAME);
        if (endpoint.equals(Endpoint.UNKNOWN)) {
            String requestPath = exchange.getRequestURI().getPath();
            String[] pathParts = requestPath.split("/", -1);
            if (pathParts.length == 4 && "subtasks".equals(pathParts[pathParts.length - 1])) {
                handleReadEpicSubtasks(exchange);
                return;
            }
        }
        handleExecute(exchange, endpoint);
    }

    void handleReadEpicSubtasks(HttpExchange exchange) throws IOException {
        try {
            int epicId = parseTaskIdFromUri(exchange);
            String body = gson.toJson(taskManager.getEpicSubtasks(epicId).values(), List.class);
            sendResponse(exchange, body, 200);
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    @Override
    void handleReadAll(HttpExchange exchange) throws IOException {
        String body = gson.toJson(taskManager.getEpics().values(), List.class);
        sendResponse(exchange, body, 200);
    }

    @Override
    void handleReadOne(HttpExchange exchange) throws IOException {
        try {
            int epicId = parseTaskIdFromUri(exchange);
            if (taskManager.getEpics().containsKey(epicId)) {
                String body = gson.toJson(taskManager.getEpic(epicId));
                sendResponse(exchange, body, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    @Override
    void handleCreate(HttpExchange exchange) throws IOException {
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            Epic epic = Epic.cloneWithNextId(gson.fromJson(jsonObject, Epic.class));
            taskManager.addEpic(epic);
            sendResponse(exchange, 201);
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    @Override
    void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            Epic epic = gson.fromJson(jsonObject, Epic.class);

            if (taskManager.getEpics().containsKey(epic.getId())) {
                taskManager.updateEpic(epic);
                sendResponse(exchange, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    @Override
    void handleDelete(HttpExchange exchange) throws IOException {
        try {
            int epicId = parseTaskIdFromUri(exchange);
            if (taskManager.getEpics().containsKey(epicId)) {
                taskManager.removeEpic(epicId);
                sendResponse(exchange, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }
}
