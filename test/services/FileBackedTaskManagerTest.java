package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File taskManagerDataFile;
    TaskManager taskManager;

    @BeforeEach
    public void init() {
        try {
            taskManagerDataFile = File.createTempFile("testData", ".csv");
            taskManager = new FileBackedTaskManager(Managers.getDefaultHistoryManager(), taskManagerDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void epicsNotNullAndEmptyFromEmptyFile() {
        Map<Integer, Epic> tasks = taskManager.getEpics();

        assertNotNull(tasks, "Объект - null");
        assertEquals(0, tasks.size(), "Неверное количество элементов.");
    }

    @Test
    public void subtasksNotNullAndEmptyFromEmptyFile() {
        Map<Integer, Subtask> tasks = taskManager.getSubtasks();

        assertNotNull(tasks, "Объект - null");
        assertEquals(0, tasks.size(), "Неверное количество элементов.");
    }

    @Test
    public void tasksNotNullAndEmptyFromEmptyFile() {
        Map<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Объект - null");
        assertEquals(0, tasks.size(), "Неверное количество элементов.");
    }

    @Test
    public void historyNotNullAndEmptyFromEmptyFile() {
        List<Task> history = taskManager.getHistory();

        assertNotNull(history, "Объект - null");
        assertEquals(0, history.size(), "Неверное количество элементов.");
    }

    @Test
    public void saveTaskToEmptyFile() {
        long before = taskManagerDataFile.length();

        Task singleTask1 = new Task("Сходить за грибами", "Выйти не позднее 7.00");
        taskManager.addTask(singleTask1);
        long after = taskManagerDataFile.length();

        assertTrue(after > before);
    }

    @Test
    public void loadTask() {
        Task task = new Task("Прогуляться", "");
        taskManager.addTask(task);
        taskManager = null;

        TaskManager newManager = FileBackedTaskManager.loadFromFile(taskManagerDataFile);
        Map<Integer, Task> tasks = newManager.getTasks();

        assertEquals(1, tasks.size(), "Неверное количество элементов.");
    }

    @Test
    public void loadHistory() {
        Task task = new Task("Выпить кофе", "");
        taskManager.addTask(task);
        taskManager.getTask(task.getId());
        taskManager = null;

        TaskManager newManager = FileBackedTaskManager.loadFromFile(taskManagerDataFile);
        List<Task> history = newManager.getHistory();

        assertEquals(1, history.size(), "Неверное количество элементов.");
    }

    @Test
    public void checkNextTaskIdAfterCreatingFromString() {
        String taskString = "100,Прочитать новости,,NEW";
        Task createdFromStringTask = Task.createFromString(taskString, ",");
        int createdFromStringTaskId = createdFromStringTask.getId();

        Task nextTask = new Task("Выпить кофе", "");
        int nextTaskId = nextTask.getId();

        assertTrue(nextTaskId > createdFromStringTaskId);
        assertEquals(1, nextTaskId - createdFromStringTaskId);
    }
}