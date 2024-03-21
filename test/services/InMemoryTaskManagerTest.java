package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    final TaskManager taskManager = Managers.getDefaultTaskManager();
    static Task lastTask;
    static Task lastEpic;
    static Task lastSubtask;

    @BeforeEach
    public void addTasks() {
        Task[] tasks = new Task[]{
                new Task("Сходить за грибами", "Выйти не позднее 7.00"),
                new Task("Купить билеты для отпуска", "")
        };
        for (Task task : tasks) {
            taskManager.addTask(task);
            lastTask = task;
        }
    }

    @BeforeEach
    public void addEpics() {
        Epic[] epics = new Epic[]{
                new Epic("Важный эпик 1", "Описание Эпика1"),
                new Epic("Завершить переезд", "Управиться за неделю")
        };
        for (Epic epic : epics) {
            taskManager.addEpic(epic);
            lastEpic = epic;
        }
    }

    @BeforeEach
    public void addSubtasksToLastEpic() {
        int epicId = lastEpic.getId();
        Subtask[] subtasks = new Subtask[]{
                new Subtask("Собрать коробки", "", Status.NEW, epicId),
                new Subtask("Упаковать кошку", "", Status.IN_PROGRESS, epicId),
                new Subtask("Сказать прощальные слова", "", Status.NEW, epicId)
        };
        for (Subtask subtask : subtasks) {
            taskManager.addSubtask(subtask);
            lastSubtask = subtask;
        }
    }

    @Test
    void getTasksEpicsSubtasks() {
        assertFalse(taskManager.getEpics().isEmpty(), "Пусто.");
        assertFalse(taskManager.getSubtasks().isEmpty(), "Пусто.");
        assertFalse(taskManager.getTasks().isEmpty(), "Пусто.");
    }

    @Test
    void epicAndSubtaskCanNotBeAddedToTasks() {
        int before = taskManager.getTasks().size();
        taskManager.addTask(new Epic("NEW", ""));
        taskManager.addTask(new Subtask("NEW", "", Status.NEW, lastEpic.getId()));
        int after = taskManager.getTasks().size();
        assertEquals(before, after);
    }

    @Test
    void subtaskCanNotUseTaskOrSubtaskId() {
        int before = taskManager.getSubtasks().size();
        taskManager.addSubtask(new Subtask("NEW", "", Status.NEW, lastTask.getId()));
        taskManager.addSubtask(new Subtask("NEW", "", Status.NEW, lastSubtask.getId()));
        int after = taskManager.getSubtasks().size();
        assertEquals(before, after);
    }

    @Test
    void equalsById() {
        assertEquals(lastSubtask, (Task) lastSubtask);
        assertEquals(lastEpic, (Task) lastEpic);
        assertEquals(lastSubtask, taskManager.getSubtask(lastSubtask.getId()));
        assertEquals(lastTask, taskManager.getTask(lastTask.getId()));
    }

    @Test
    void clearEpics() {
        taskManager.clearEpics();
        assertTrue(taskManager.getEpics().isEmpty(), "Не пусто.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Не пусто.");
    }

    @Test
    void clearAllData() {
        taskManager.clearAllData();
        assertTrue(taskManager.getTasks().isEmpty(), "Не пусто.");
        assertTrue(taskManager.getEpics().isEmpty(), "Не пусто.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Не пусто.");
    }
}