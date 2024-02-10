public class Main {
    static TaskManager manager;

    public static void main(String[] args) {
        manager = new TaskManager();

        Task singleTask1 = new Task("Сходить на работу", "Выйти не позднее 7.00");
        Task singleTask2 = new Task("Купить билеты для отпуска", "");
        Task singleTask3 = new Task("Сходить в театр", "");
        manager.addTask(singleTask1);
        manager.addTask(singleTask2);
        manager.addTask(singleTask3);
        manager.removeTask(singleTask3.getId());

        singleTask1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(singleTask1);

//        System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Переезд", "ОписаниеЭпика1");
        Subtask subtask1 = new Subtask("Собрать коробки", "", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Упаковать кошку", "", Status.IN_PROGRESS, epic1.getId());
        Subtask subtask3 = new Subtask("Сказать прощальные слова", "", Status.NEW, epic1.getId());
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.addEpic(epic1);
        manager.removeSubtask(subtask2.getId());
        //manager.clearEpicSubtasks(4);

        //System.out.println(manager.getEpicSubtasks(epic1.getId()));
        //System.out.println(manager.getEpicById(epic1.getId()));

        int epic2 = manager.addEpic(new Epic("Важный эпик 2", "ОписаниеЭпика2"));
        int subtask21 = manager.addSubtask(new Subtask("Задача1", "", Status.DONE, epic2));
        int subtask22 = manager.addSubtask(new Subtask("Задача2", "", Status.DONE, epic2));
        manager.removeEpic(epic2);
        //manager.clearAllSubtasks();
        //manager.clearAllData();

        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

    }
}