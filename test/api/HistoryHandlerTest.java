package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HistoryHandlerTest extends ApiTest {
    public static final String PATH = "/" + HistoryHandler.PATH_NAME;

    @Test
    void handle_responseCodeShouldBe200IfUriIsCorrect() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void handle_responseListAfterGetTaskByIdShouldBeNonEmpty() {
        manager.clearAllData();
        Task task = new Task("Задача");
        manager.addTask(task);

        manager.getTask(task.getId());
        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Task> tasks = gson.fromJson(jsonElement, new TaskListTypeToken().getType());

        assertFalse(tasks.isEmpty());
    }

    @Test
    void handle_responseCodeShouldBe500IfUriIsIncorrect() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/1s");
        int statusCode = response.statusCode();

        assertEquals(500, statusCode);
    }

    static class TaskListTypeToken extends TypeToken<ArrayList<Task>> {
    }
}