package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    final TaskManager taskManager = Managers.getDefaultTaskManager();
    static Task lastTask;
    static Epic lastEpic;
    static Subtask lastSubtask;

    @BeforeEach
    public void addTask() {
        lastTask = new Task("Купить билеты для отпуска", "");
        taskManager.addTask(lastTask);
    }

    @BeforeEach
    public void addEpic() {
        lastEpic = new Epic("Завершить переезд", "Управиться за неделю");
        taskManager.addEpic(lastEpic);
    }

    @BeforeEach
    public void addSubtasks() {
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
    void addHistoryWithoutDuplicates() {
        taskManager.getTask(lastTask.getId());
        taskManager.getEpic(lastEpic.getId());
        taskManager.getSubtask(lastSubtask.getId());
        assertEquals(3, taskManager.getHistory().size(), "Неверное количество элементов в истории.");
    }

    @Test
    void addHistoryWithDuplicates() {
        taskManager.getTask(lastTask.getId());
        taskManager.getTask(lastTask.getId());
        assertEquals(1, taskManager.getHistory().size(), "Неверное количество элементов в истории.");
    }

    @Test
    void checkLastElementIdWithDuplicates() {
        taskManager.getTask(lastTask.getId());
        taskManager.getEpic(lastEpic.getId());
        taskManager.getSubtask(lastSubtask.getId());
        assertEquals(lastSubtask.getId(), taskManager.getHistory().getLast().getId(), "Неверный id элемента.");

        taskManager.getTask(lastTask.getId());
        assertEquals(lastTask.getId(), taskManager.getHistory().getLast().getId(), "Неверный id элемента.");
    }

    @Test
    void changeHistorySizeAfterTaskDelete() {
        taskManager.getTask(lastTask.getId());
        taskManager.getEpic(lastEpic.getId());
        taskManager.getSubtask(lastSubtask.getId());
        assertEquals(3, taskManager.getHistory().size(), "Неверное количество элементов в истории.");

        taskManager.removeTask(lastTask.getId());
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество элементов в истории.");
    }

    @Test
    void removeSubtasksAfterEpicDelete() {
        taskManager.getEpic(lastEpic.getId());
        taskManager.getSubtask(lastSubtask.getId());
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество элементов в истории.");

        taskManager.removeEpic(lastEpic.getId());
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество элементов в истории.");
    }
}