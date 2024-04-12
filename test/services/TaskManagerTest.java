package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    public void epicStatusShouldBeNewWithoutSubtasks() {
        Epic epic = new Epic("Epic", "");
        taskManager.addEpic(epic);

        assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void epicStatusShouldBeNewWithAllNewSubtasks() {
        Epic epic = new Epic("Epic", "");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Subtask1", "", Status.NEW, epic.getId()));
        taskManager.addSubtask(new Subtask("Subtask2", "", Status.NEW, epic.getId()));

        assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void epicStatusShouldBeDoneWithAllDoneSubtasks() {
        Epic epic = new Epic("Epic", "");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Subtask1", "", Status.DONE, epic.getId()));
        taskManager.addSubtask(new Subtask("Subtask2", "", Status.DONE, epic.getId()));

        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void epicStatusShouldBeInProgressWithNewAndDoneSubtasks() {
        Epic epic = new Epic("Epic", "");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Subtask1", "", Status.NEW, epic.getId()));
        taskManager.addSubtask(new Subtask("Subtask2", "", Status.DONE, epic.getId()));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void epicStatusShouldBeInProgressWithInProgressSubtasks() {
        Epic epic = new Epic("Epic", "");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Subtask1", "", Status.IN_PROGRESS, epic.getId()));
        taskManager.addSubtask(new Subtask("Subtask2", "", Status.IN_PROGRESS, epic.getId()));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void ShouldBeImpossibleToAddTaskWhenTheDatesIntersects() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task1", "", "NEW", "08.05.2024 06:00", 60));
        taskManager.addTask(new Task("Task2", "", "NEW", "08.05.2024 07:00", 60));
        taskManager.addTask(new Task("Task3", "", "NEW", "08.05.2024 09:00", 60));
        taskManager.addTask(new Task("Task4", "", "NEW", "08.05.2024 05:00", 60));

        assertEquals(4, taskManager.getTasks().size());

        taskManager.addTask(new Task("Task5", "", "NEW", "08.05.2024 05:00", 60));
        taskManager.addTask(new Task("Task6", "", "NEW", "08.05.2024 07:30", 60));

        assertEquals(4, taskManager.getTasks().size());
    }
}
