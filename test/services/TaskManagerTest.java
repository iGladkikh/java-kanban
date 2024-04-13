package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        taskManager.addTask(new Task("Task1", "", "NEW", "08.05.2024 08:00", 60));
        taskManager.addTask(new Task("Task2", "", "NEW", "08.05.2024 07:00", 60));
        taskManager.addTask(new Task("Task3", "", "NEW", "08.05.2024 06:00", 60));
        taskManager.addTask(new Task("Task4", "", "NEW", "08.05.2024 05:00", 60));

        assertEquals(4, taskManager.getTasks().size());
        assertEquals(4, taskManager.getPrioritizedTasks().size());

        taskManager.addTask(new Task("Task5", "", "NEW", "08.05.2024 05:00", 60));
        taskManager.addTask(new Task("Task6", "", "NEW", "08.05.2024 07:30", 60));

        assertEquals(4, taskManager.getTasks().size());
        assertEquals(4, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void prioritizedTasksShouldBeEmptyWhenAddsTasksWithoutStartTime() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task1", "", Status.NEW));
        taskManager.addTask(new Task("Task2", "", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Task3", "", Status.DONE));
        taskManager.addTask(new Task("Task4", "", Status.NEW));

        assertEquals(4, taskManager.getTasks().size());
        assertEquals(0, taskManager.getPrioritizedTasks().size());

        taskManager.addTask(new Task("Task5", "", "NEW", "08.05.2024 05:00", 60));
        taskManager.addTask(new Task("Task6", "", "NEW", "08.05.2024 07:30", 60));

        assertEquals(6, taskManager.getTasks().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void prioritizedTasksShouldBeSortedByStartTime() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task1", "", "NEW", "08.05.2024 08:00", 60));
        taskManager.addTask(new Task("Task2", "", "NEW", "08.05.2024 07:00", 60));
        taskManager.addTask(new Task("Task3", "", "NEW", "08.05.2024 06:00", 60));
        taskManager.addTask(new Task("Task4", "", "NEW", "08.05.2024 05:00", 60));

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks().stream().toList();
        for (int i = 1; i < prioritizedTasks.size(); i++) {
            assertTrue(prioritizedTasks.get(i - 1).getStartTime()
                    .isBefore(prioritizedTasks.get(i).getStartTime()));
        }
    }
}
