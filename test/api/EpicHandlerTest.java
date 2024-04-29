package api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest extends ApiTest {
    static final String PATH = "/" + EpicHandler.PATH_NAME;
    static Epic defaultEpic;
    static JsonObject defaultJsonEpic;

    @BeforeAll
    static void init() {
        defaultEpic = new Epic("Новый эпик");
        defaultJsonEpic = new JsonObject();
        defaultJsonEpic.addProperty("name", defaultEpic.getName());
    }

    @BeforeEach
    void resetEpics() {
        manager.clearEpics();
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
    void handleReadAll_managersAndApiReceivedEpicsListsShouldBeTheSameSize() {
        manager.addEpic(defaultEpic);

        HttpResponse<String> response = sendGetRequest(HOST + PATH);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        List<Epic> receivedEpics = gson.fromJson(jsonElement, new EpicListTypeToken().getType());

        assertEquals(manager.getEpics().size(), receivedEpics.size());
    }

    @Test
    void handleReadOne_managersAndApiReceivedEpicsShouldBeEquals() {
        manager.addEpic(defaultEpic);

        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + defaultEpic.getId());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        Epic responsedEpic = gson.fromJson(jsonElement, Epic.class);

        assertEquals(defaultEpic, responsedEpic);
    }

    @Test
    void handleReadOne_responseCodeShouldBe404IfRequestedEpicIsNotAvailable() {
        HttpResponse<String> response = sendGetRequest(HOST + PATH + "/" + 9999);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleCreate_responseCodeShouldBe201IfEpicWasCreated() {
        String jsonEpic = defaultJsonEpic.toString();
        HttpResponse<String> response = sendPostRequest(HOST + PATH, jsonEpic);

        int statusCode = response.statusCode();

        assertEquals(201, statusCode);
    }

    @Test
    void handleCreate_numberOfEpicsAfterCreationShouldBeGreaterThanBefore() {
        int epicsCountBefore = manager.getEpics().size();
        String jsonEpic = defaultJsonEpic.toString();

        sendPostRequest(HOST + PATH, jsonEpic);
        int epicsCountAfter = manager.getEpics().size();

        assertTrue(epicsCountAfter > epicsCountBefore);
    }


    @Test
    void handleUpdate_shouldBeDifferentFieldValueAfterUpdate() {
        String nameBefore = "Новая подзадача";
        Epic epic = new Epic(nameBefore);
        manager.addEpic(epic);

        String nameAfter = "Обновленная подзадача";
        String updatedJsonEpic = gson.toJson(epic).replaceFirst(nameBefore, nameAfter);
        sendPostRequest(HOST + PATH, updatedJsonEpic);

        assertNotEquals(nameBefore, manager.getEpic(epic.getId()).getName());
    }

    @Test
    void handleUpdate_responseCodeShouldBe404IfUpdatedEpicIsNotAvailable() {
        manager.addEpic(defaultEpic);

        String updatedJsonEpic = gson.toJson(defaultEpic)
                .replaceFirst(String.valueOf(defaultEpic.getId()), "99999");
        HttpResponse<String> response = sendPostRequest(HOST + PATH, updatedJsonEpic);
        int statusCode = response.statusCode();

        assertEquals(404, statusCode);
    }

    @Test
    void handleDelete_numberOfEpicsAfterDeletionShouldBeLessThanBefore() {
        manager.addEpic(defaultEpic);
        int epicsCountBefore = manager.getEpics().size();

        sendDeleteRequest(HOST + PATH + "/" + defaultEpic.getId());
        int epicsCountAfter = manager.getEpics().size();

        assertTrue(epicsCountAfter < epicsCountBefore);
    }

    @Test
    void handleReadEpicSubtasks_responseCodeShouldBe200IfUriIsCorrect() {
        String uri = HOST + PATH + "/" + defaultEpic.getId() + "/subtasks";
        HttpResponse<String> response = sendGetRequest(uri);
        int statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    static class EpicListTypeToken extends TypeToken<ArrayList<Epic>> {
    }
}