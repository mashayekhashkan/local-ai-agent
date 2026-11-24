package at.aiagent.service;

import at.aiagent.model.Task;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderService {

    private final TaskService taskService;

    public ReminderService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void checkReminder() throws SQLException {

        List<Task> dueTask = taskService.getDueTasks();

        if (dueTask.isEmpty()) {
            return;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Task task : dueTask) {

            System.out.println("\n==============================");
            System.out.println("ERINNERUNG!");
            System.out.println("Aufgabe: " + task.getTitle());
            System.out.println("Priorität: " + task.getPriority());
            System.out.println("Fällig seit: "
                    + task.getReminder().format(formatter));
            System.out.println("==============================\n");

            taskService.markReminderAsSent(task.getId());
        }
    }
}
