import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import services.Managers;
import services.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    static TaskManager manager;

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks().values()) {
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : manager.getEpics().values()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId()).values()) {
                System.out.println("\t--> " + task);
            }
        }

        System.out.println("\nПодзадачи:");
        for (Task subtask : manager.getSubtasks().values()) {
            System.out.println(subtask);
        }

        System.out.println("\nИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {
        manager = Managers.getDefaultTaskManager();

        Task singleTask1 = new Task("Сходить за грибами", "Выйти не позднее 7.00");
        Task singleTask2 = new Task("Купить билеты для отпуска", "");
        Task singleTask3 = new Task("Сходить в театр", "");
        manager.addTask(singleTask1);
        manager.addTask(singleTask2);
        manager.addTask(singleTask3);
        //manager.removeTask(singleTask3.getId());

        singleTask1.setStatus(Status.IN_PROGRESS);
        //manager.updateTask(singleTask1);

//        System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Переезд", "Описание Эпика1");
        Subtask subtask1 = new Subtask("Собрать коробки", "", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Упаковать кошку", "", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Сказать прощальные слова", "", Status.NEW, epic1.getId());
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addEpic(epic1);
        //manager.removeSubtask(subtask2.getId());
        //manager.clearEpicSubtasks(4);

        //System.out.println(manager.getEpicSubtasks(epic1.getId()));
        //System.out.println(manager.getEpicById(epic1.getId()));

        int epic2 = manager.addEpic(new Epic("Важный эпик 2", "Описание Эпика2"));
        int subtask21 = manager.addSubtask(new Subtask("Задача1", "", Status.DONE, epic2));
        int subtask22 = manager.addSubtask(new Subtask("Задача2", "", Status.DONE, epic2));

//        manager.getEpic(epic2);
//        manager.getEpic(epic2);
        //manager.removeEpic(epic2);
        //manager.clearAllSubtasks();
        //manager.clearAllData();

        manager.getTask(singleTask3.getId());
        manager.getSubtask(subtask22);
        manager.getSubtask(subtask21);
        manager.getEpic(epic2);
        manager.getTask(singleTask3.getId());
        manager.getEpic(epic2);
        manager.removeEpic(epic2);

        printAllTasks(manager);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        String body = gson.toJson(manager.getTasks());
        System.out.print(body);
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = Task.DATE_TIME_FORMATTER;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(duration.toMinutes());
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.ofMinutes(jsonReader.nextLong());
        }
    }
}