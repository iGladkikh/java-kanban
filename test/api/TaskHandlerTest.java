package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest extends ApiTest {
    public static final String PATH = "/" + TaskHandler.PATH_NAME;
    static Task defaultTask;
    static JsonObject defaultJsonTask;

    @BeforeAll
    static void init() {
        defaultTask = new Task("Новая задача");
        defaultJsonTask = new JsonObject();
        defaultJsonTask.addProperty("name", defaultTask.getName());
    }

    @BeforeEach
    void resetTasks() {
        manager.clearTasks();
    }

    @Test
    void handleReadAll_responseCodeShouldBe200IfUriIsCorrect() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    void handleReadAll_responseCodeShouldBe500IfUriIsIncorrect() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/1s");
        int statusCode = response.statusCode();

        assertEquals(500, statusCode);
    }

    @Test
    void handleReadAll_managersAndApiReceivedTasksListsShouldBeTheSameSize() {
        manager.addTask(defaultTask);

        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Task> receivedTasks = gson.fromJson(jsonElement, new TaskListTypeToken().getType());

        assertEquals(manager.getTasks().size(), receivedTasks.size());
    }

    @Test
    void handleReadOne_managersAndApiReceivedTasksShouldBeEquals() {
        manager.addTask(defaultTask);

        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + defaultTask.getId());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Task responsedTask = gson.fromJson(jsonElement, Task.class);

        assertEquals(defaultTask, responsedTask);
    }

    @Test
    void handleReadOne_responseCodeShouldBe404IfRequestedTaskIsNotAvailable() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + 9999);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleCreate_responseCodeShouldBe201IfTaskWasCreated() {
        HttpResponse<String> response = sendPostRequest(HOST + PATH, defaultJsonTask.toString());

        int statusCode = response.statusCode();

        assertEquals(201, statusCode);
    }

    @Test
    void handleCreate_numberOfTasksAfterCreationShouldBeGreaterThanBefore() {
        int tasksCountBefore = manager.getTasks().size();

        sendPostRequest(HOST + PATH, defaultJsonTask.toString());
        int tasksCountAfter = manager.getTasks().size();

        assertTrue(tasksCountAfter > tasksCountBefore);
    }

    @Test
    void handleCreate_responseCodeShouldBe406WhenTimeOfNewTaskIntersectsWithAnotherTask() {
        Task task = new Task("Первая задача", "", "NEW",
                "12.09.2024 12:00", 60);
        manager.addTask(task);

        JsonObject intersectedTask = defaultJsonTask;
        intersectedTask.addProperty("startTime", "12.09.2024 12:30");
        intersectedTask.addProperty("duration", 30);
        HttpResponse<String> response = sendPostRequest(HOST + PATH, intersectedTask.toString());

        int statusCode = response.statusCode();

        assertEquals(406, statusCode);
    }

    @Test
    void handleUpdate_shouldBeDifferentFieldValueAfterUpdate() {
        String nameBefore = "Новая задача";
        Task task = new Task(nameBefore);
        manager.addTask(task);

        String nameAfter = "Обновленная задача";
        String updatedJsonTask = gson.toJson(task).replaceFirst(nameBefore, nameAfter);
        sendPostRequest(HOST + PATH, updatedJsonTask);
        String nameAfterUpdate = manager.getTask(task.getId()).getName();

        assertNotEquals(nameBefore, nameAfterUpdate);
    }

    @Test
    void handleUpdate_responseCodeShouldBe404IfUpdatedTaskIsNotAvailable() {
        manager.addTask(defaultTask);

        String updatedJsonTask = gson.toJson(defaultTask)
                .replaceFirst(String.valueOf(defaultTask.getId()), "99999");
        HttpResponse<String> response = sendPostRequest(HOST + PATH, updatedJsonTask);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleDelete_numberOfTasksAfterDeletionShouldBeLessThanBefore() {
        manager.addTask(defaultTask);
        int tasksCountBefore = manager.getTasks().size();

        sendDeleteRequest(HOST + PATH + "/" + defaultTask.getId());
        int tasksCountAfter = manager.getTasks().size();

        assertTrue(tasksCountAfter < tasksCountBefore);
    }

    static class TaskListTypeToken extends TypeToken<ArrayList<Task>> {
    }
}