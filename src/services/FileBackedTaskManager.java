package services;

import exceptions.ManagerFileCreateException;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.Type;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.getProperty;
import static java.lang.System.out;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String[] DATA_FILE_HEADERS = new String[]{"type", "id", "name", "description", "status",
            "start_time", "duration", "epic"};
    private static final Charset DATA_FILE_CHARSET = StandardCharsets.UTF_8;
    private static final String DATA_FILE_DELIMITER = ",";
    private final File dataFile;

    FileBackedTaskManager(HistoryManager historyManager, File dataFile) {
        super(historyManager);
        this.dataFile = dataFile;
        if (Files.exists(dataFile.toPath())) {
            fillDataFromFile();
        } else {
            createDataFile();
        }
    }

    public static void main(String[] args) {
        String dataFileDirectory = Paths.get(getProperty("user.dir"), "data").toString();
        String testManagerDataFile = "testManagerData.csv";
        TaskManager manager = FileBackedTaskManager.loadFromFile(Paths.get(dataFileDirectory, testManagerDataFile).toFile());

        out.println(manager.getEpics());
        out.println(manager.getSubtasks());
        out.println(manager.getTasks());
        out.println(manager.getPrioritizedTasks());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (Files.exists(file.toPath())) {
            return new FileBackedTaskManager(Managers.getDefaultHistoryManager(), file);
        }
        throw new ManagerLoadException("Файл '" + file + "' отсутствует.");
    }

    static String historyToString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(tasks.get(i).getId());
            if (i < tasks.size() - 1) {
                sb.append(DATA_FILE_DELIMITER);
            }
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] ids = value.split(DATA_FILE_DELIMITER);
        List<Integer> res = new ArrayList<>(ids.length);
        for (String id : ids) {
            res.add(Integer.parseInt(id));
        }
        return res;
    }

    private void createDataFile() {
        try {
            Files.createFile(dataFile.toPath());
        } catch (IOException e) {
            throw new ManagerFileCreateException("Ошибка создания файла: " + dataFile);
        }
    }

    private void fillDataFromFile() {
        try (FileReader reader = new FileReader(dataFile, DATA_FILE_CHARSET);
             BufferedReader br = new BufferedReader(reader)) {
            String epicType = Type.EPIC.toString();
            String subtaskType = Type.SUBTASK.toString();
            String taskType = Type.TASK.toString();
            Map<Integer, Task> loadedTasks = new HashMap<>();
            List<Integer> loadedHistory = new ArrayList<>();

            int i = 0;
            while (br.ready()) {
                StringBuilder sb = new StringBuilder(br.readLine());
                if (i > 0 && !sb.isEmpty()) {
                    int firstDelimiterIndex = sb.indexOf(DATA_FILE_DELIMITER);
                    String startWorld = firstDelimiterIndex > -1 ? sb.substring(0, firstDelimiterIndex) : null;
                    String taskData = sb.substring(firstDelimiterIndex + 1);

                    if (epicType.equals(startWorld)) {
                        Epic epic = Epic.createFromString(taskData, DATA_FILE_DELIMITER);
                        super.addEpic(epic);
                        loadedTasks.put(epic.getId(), epic);
                    } else if (subtaskType.equals(startWorld)) {
                        Subtask subtask = Subtask.createFromString(taskData, DATA_FILE_DELIMITER);
                        super.addSubtask(subtask);
                        loadedTasks.put(subtask.getId(), subtask);
                    } else if (taskType.equals(startWorld)) {
                        Task task = Task.createFromString(taskData, DATA_FILE_DELIMITER);
                        super.addTask(task);
                        loadedTasks.put(task.getId(), task);
                    } else {
                        loadedHistory.addAll(historyFromString(sb.toString()));
                    }
                }
                i++;
            }
            for (Integer id : loadedHistory) {
                historyManager.add(loadedTasks.get(id));
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения файла: " + dataFile);
        }
    }

    private void save() {
        saveFileHeader();
        saveData();
    }

    private void saveFileHeader() {
        try (Writer fileWriter = new FileWriter(dataFile, DATA_FILE_CHARSET)) {
            fileWriter.write(String.join(DATA_FILE_DELIMITER, DATA_FILE_HEADERS));
        } catch (IOException e) {
            throw new ManagerFileCreateException("Ошибка записи в файл: " + dataFile);
        }
    }

    private void saveData() {
        try (Writer fileWriter = new FileWriter(dataFile, DATA_FILE_CHARSET, true)) {
            for (Task epic : getEpics().values()) {
                fileWriter.write("\n" + epic.toSaveString(DATA_FILE_DELIMITER));
            }

            for (Task subtask : getSubtasks().values()) {
                fileWriter.write("\n" + subtask.toSaveString(DATA_FILE_DELIMITER));
            }

            for (Task task : getTasks().values()) {
                fileWriter.write("\n" + task.toSaveString(DATA_FILE_DELIMITER));
            }

            fileWriter.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + dataFile);
        }
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void clearEpicSubtasks(int id) {
        super.clearEpicSubtasks(id);
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearAllData() {
        super.clearAllData();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }
}