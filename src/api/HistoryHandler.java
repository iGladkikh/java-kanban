package api;

import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;

import java.io.IOException;

public class HistoryHandler extends Handler {
    static final String PATH_NAME = "history";

    HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = getRequestPathParts(exchange);
        String requestMethod = exchange.getRequestMethod();

        if (pathParts.length == 2 && requestMethod.equals("GET") && PATH_NAME.equals(pathParts[1])) {
            String body = gson.toJson(taskManager.getHistory());
            sendResponse(exchange, body, 200);
        } else {
            sendResponse(exchange, 500);
        }
    }
}
