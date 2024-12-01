package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest extends ApiTest {
    static final String PATH = "/" + SubtaskHandler.PATH_NAME;
    static Epic defaultEpic;
    static Subtask defaultSubtask;
    static JsonObject defaultJsonSubtask;

    @BeforeAll
    static void addEpic() {
        defaultEpic = new Epic("Новый эпик");
        manager.addEpic(defaultEpic);

        defaultSubtask = new Subtask("Новая подзадача", "", Status.NEW, defaultEpic.getId());

        defaultJsonSubtask = new JsonObject();
        defaultJsonSubtask.addProperty("name", "Подзадача эпика #" + defaultEpic.getId());
        defaultJsonSubtask.addProperty("epicId", defaultEpic.getId());
    }

    @BeforeEach
    void resetSubtasks() {
        manager.clearSubtasks();
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
    void handleReadAll_managersAndApiReceivedSubtasksListsShouldBeTheSameSize() {
        manager.addSubtask(defaultSubtask);

        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Subtask> receivedSubtasks = gson.fromJson(jsonElement, new SubtaskListTypeToken().getType());

        assertEquals(manager.getSubtasks().size(), receivedSubtasks.size());
    }

    @Test
    void handleReadOne_managersAndApiReceivedSubtasksShouldBeEquals() {
        manager.addSubtask(defaultSubtask);

        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + defaultSubtask.getId());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Subtask responsedSubtask = gson.fromJson(jsonElement, Subtask.class);

        assertEquals(defaultSubtask, responsedSubtask);
    }

    @Test
    void handleReadOne_responseCodeShouldBe404IfRequestedSubtaskIsNotAvailable() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + 9999);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleCreate_responseCodeShouldBe201IfSubtaskWasCreated() {
        String jsonSubtask = defaultJsonSubtask.toString();
        HttpResponse<String> response = sendPostRequest(HOST + PATH, jsonSubtask);

        int statusCode = response.statusCode();

        assertEquals(201, statusCode);
    }

    @Test
    void handleCreate_numberOfSubtasksAfterCreationShouldBeGreaterThanBefore() {
        int subtasksCountBefore = manager.getSubtasks().size();
        String jsonSubtask = defaultJsonSubtask.toString();

        sendPostRequest(HOST + PATH, jsonSubtask);
        int subtasksCountAfter = manager.getSubtasks().size();

        assertTrue(subtasksCountAfter > subtasksCountBefore);
    }

    @Test
    void handleCreate_responseCodeShouldBe406WhenNewSubtaskIntersectsWithAnotherSubtask() {
        Subtask subtask = new Subtask("Подзадача", "",
                "NEW", "09.12.2024 15:00", 60, defaultEpic.getId());
        manager.addSubtask(subtask);

        JsonObject intersectedSubtask = defaultJsonSubtask;
        intersectedSubtask.addProperty("startTime", "09.12.2024 15:30");
        intersectedSubtask.addProperty("duration", 30);
        HttpResponse<String> response = sendPostRequest(HOST + PATH, intersectedSubtask.toString());

        int statusCode = response.statusCode();

        assertEquals(406, statusCode);
    }

    @Test
    void handleUpdate_shouldBeDifferentFieldValueAfterUpdate() {
        String nameBefore = "Новая подзадача";
        Subtask subtask = new Subtask(nameBefore, "", Status.NEW, defaultEpic.getId());
        manager.addSubtask(subtask);

        String nameAfter = "Обновленная подзадача";
        String updatedJsonSubtask = gson.toJson(subtask).replaceFirst(nameBefore, nameAfter);
        sendPostRequest(HOST + PATH, updatedJsonSubtask);

        assertNotEquals(nameBefore, manager.getSubtask(subtask.getId()).getName());
    }

    @Test
    void handleUpdate_responseCodeShouldBe404IfUpdatedSubtaskIsNotAvailable() {
        manager.addSubtask(defaultSubtask);

        String updatedJsonSubtask = gson.toJson(defaultSubtask)
                .replaceFirst(String.valueOf(defaultSubtask.getId()), "99999");
        HttpResponse<String> response = sendPostRequest(HOST + PATH, updatedJsonSubtask);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleDelete_numberOfSubtasksAfterDeletionShouldBeLessThanBefore() {
        manager.addSubtask(defaultSubtask);
        int subtasksCountBefore = manager.getSubtasks().size();

        sendDeleteRequest(HOST + PATH + "/" + defaultSubtask.getId());
        int subtasksCountAfter = manager.getSubtasks().size();

        assertTrue(subtasksCountAfter < subtasksCountBefore);
    }

    static class SubtaskListTypeToken extends TypeToken<ArrayList<Subtask>> {
    }
}