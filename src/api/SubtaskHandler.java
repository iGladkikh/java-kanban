package api;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends TaskHandler {
    static final String PATH_NAME = "subtasks";

    SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange, PATH_NAME);
        handleExecute(exchange, endpoint);
    }

    @Override
    void handleReadAll(HttpExchange exchange) throws IOException {
        String body = gson.toJson(taskManager.getSubtasks().values(), List.class);
        sendResponse(exchange, body, 200);
    }

    @Override
    void handleReadOne(HttpExchange exchange) throws IOException {
        try {
            int subtaskId = parseTaskIdFromUri(exchange);
            if (taskManager.getSubtasks().containsKey(subtaskId)) {
                String body = gson.toJson(taskManager.getSubtask(subtaskId));
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
            Subtask subtask = Subtask.cloneWithNextId(gson.fromJson(jsonObject, Subtask.class));

            if (taskManager.getEpics().containsKey(subtask.getEpicId())) {
                if (taskManager.isIntersectedTask(subtask)) {
                    sendResponse(exchange, 406);
                } else {
                    taskManager.addSubtask(subtask);
                    sendResponse(exchange, 201);
                }
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }

    @Override
    void handleUpdate(HttpExchange exchange) throws IOException {
        try {
            JsonObject jsonObject = getJsonObjectFromRequestBody(exchange);
            Subtask subtask = gson.fromJson(jsonObject, Subtask.class);

            if (taskManager.getSubtasks().containsKey(subtask.getId())) {
                if (taskManager.isIntersectedTask(subtask)) {
                    sendResponse(exchange, 406);
                } else {
                    taskManager.updateSubtask(subtask);
                    sendResponse(exchange, 200);
                }
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
            int subtaskId = parseTaskIdFromUri(exchange);
            if (taskManager.getSubtasks().containsKey(subtaskId)) {
                taskManager.removeSubtask(subtaskId);
                sendResponse(exchange, 200);
            } else {
                sendResponse(exchange, 404);
            }
        } catch (Exception e) {
            sendResponse(exchange, 500);
        }
    }
}
