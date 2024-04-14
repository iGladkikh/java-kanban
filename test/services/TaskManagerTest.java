package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void shouldBeImpossibleToAddTaskWhenTheDatesIntersects() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task1", "", "NEW", "08.05.2024 08:00", 60));
        taskManager.addTask(new Task("Task2", "", "NEW", "08.05.2024 07:00", 60));
        taskManager.addTask(new Task("Task3", "", "NEW", "08.05.2024 06:00", 60));
        taskManager.addTask(new Task("Task4", "", "NEW", "08.05.2024 05:00", 60));

        int prioritizedTasksCount = taskManager.getPrioritizedTasks().size();

        taskManager.addTask(new Task("IntersectedTask5", "", "NEW", "08.05.2024 05:00", 60));
        taskManager.addTask(new Task("IntersectedTask6", "", "NEW", "08.05.2024 07:30", 60));

        assertEquals(prioritizedTasksCount, taskManager.getTasks().size());
        assertEquals(prioritizedTasksCount, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void prioritizedTasksShouldBeEmptyWhenAddsTasksWithoutStartTime() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task1", "", Status.NEW));
        taskManager.addTask(new Task("Task2", "", Status.IN_PROGRESS));
        taskManager.addTask(new Task("Task3", "", Status.DONE));

        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    public void prioritizedTasksShouldBeNonEmptyWhenAddsTasksWithStartTime() {
        taskManager.clearAllData();
        taskManager.addTask(new Task("Task", "", "NEW", "08.05.2024 05:00", 60));

        assertFalse(taskManager.getPrioritizedTasks().isEmpty());
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

    @Test
    public void epicStarTimeShouldBeEqualsToEarliestSubtaskStartTime() {
        String firstStartTime = "15.04.2024 18:25";
        String lastStartTime = "21.06.2024 11:50";

        Epic epic = new Epic("Epic", "");
        Subtask firstSubtask = new Subtask("Subtask1", "", "DONE", firstStartTime, 60, epic.getId());
        Subtask lastSubtask = new Subtask("Subtask2", "", "IN_PROGRESS", lastStartTime, 10, epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(lastSubtask);

        assertEquals(epic.getStartTime(), firstSubtask.getStartTime());
    }

    @Test
    public void epicEndTimeShouldBeEqualsToLatestSubtaskEndTime() {
        String firstStartTime = "18.03.2024 23:00";
        String lastStartTime = "28.07.2024 13:30";

        Epic epic = new Epic("Epic", "");
        Subtask firstSubtask = new Subtask("Subtask1", "", "DONE", firstStartTime, 60, epic.getId());
        Subtask lastSubtask = new Subtask("Subtask2", "", "IN_PROGRESS", lastStartTime, 10, epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(lastSubtask);

        assertEquals(epic.getEndTime(), lastSubtask.getEndTime());
    }

    @Test
    public void epicDurationShouldBeEqualsToSumOfAllSubtasksDurations() {
        String firstStartTime = "09.01.2024 03:20";
        String lastStartTime = "22.03.2024 15:30";

        Epic epic = new Epic("Epic", "");
        Subtask firstSubtask = new Subtask("Subtask1", "", "DONE", firstStartTime, 60, epic.getId());
        Subtask lastSubtask = new Subtask("Subtask2", "", "IN_PROGRESS", lastStartTime, 10, epic.getId());
        taskManager.addEpic(epic);
        taskManager.addSubtask(firstSubtask);
        taskManager.addSubtask(lastSubtask);

        assertEquals(epic.getDuration(), firstSubtask.getDuration().plus(lastSubtask.getDuration()));
    }
}
