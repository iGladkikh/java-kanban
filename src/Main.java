public class Main {
    static TaskManager manager;

    public static void main(String[] args) {
        manager = new TaskManager();

        Task singleTask1 = new Task("Сходить на работу", "Выйти не позднее 7.00");
        Task singleTask2 = new Task("Купить билеты для отпуска", "На озоне");
        Task singleTask3 = new Task("Сходить в театр", "");
        manager.addTask(singleTask1);
        manager.addTask(singleTask2);
        manager.addTask(singleTask3);
        //manager.removeTask(singleTask3.getId());

        singleTask1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(singleTask1);

        //System.out.println(manager.getTasks());

        Epic epic1 = new Epic("Переезд", "ОписаниеЭпика1");
        Subtask subtask1 = new Subtask("Собрать коробки", "", "DONE", epic1.getId());
        epic1.addSubtask(subtask1);
        epic1.addSubtask(new Task("Упаковать кошку", "", "IN_PROGRESS"));
        epic1.addSubtask(new Task("Сказать прощальные слова", "", "IN_PROGRESS"));
        manager.addEpic(epic1);
        //manager.removeSubtask(7);
        //manager.removeSubtask(9);
        //manager.clearEpicSubtasks(4);

        //System.out.println(manager.getEpicSubtasks(epic1.getId()));
        //System.out.println(manager.getEpicById(epic1.getId()));

        Epic epic2 = new Epic("Важный эпик 2", "ОписаниеЭпика2");
        epic2.addSubtask(new Task("Задача1", "", "DONE"));
        epic2.addSubtask(new Task("Задача2", "", "DONE"));
        manager.addEpic(epic2);
        //manager.removeEpic(epic1.getId());
        //manager.clearAllData();
        //System.out.println(manager.clearAllData());

        System.out.println(manager.getEpics());
    }
}
