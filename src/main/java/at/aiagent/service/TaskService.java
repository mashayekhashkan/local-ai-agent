package at.aiagent.service;

import at.aiagent.model.Task;
import at.aiagent.model.TaskPriority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final DatabaseService databaseService;

    public TaskService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Neue Aufgabe speichern
    public void addTask(Task task) throws SQLException {

        String sql = """
                INSERT INTO tasks
                (title, description, priority, reminder, completed)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getPriority().name());

            if (task.getReminder() != null) {
                statement.setString(4, task.getReminder().toString());
            } else {
                statement.setNull(4, Types.VARCHAR);
            }

            statement.setInt(5, task.isCompleted() ? 1 : 0);

            statement.executeUpdate();
        }

    }

    // Alle Aufgaben laden
    public List<Task> getAllTasks() throws SQLException {

        List<Task> tasks = new ArrayList<>();

        String sql = """
                SELECT id, title, description, priority, reminder, completed
                FROM tasks
                ORDER BY id DESC
                """;

        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                String priorityValue = resultSet.getString("priority");

                TaskPriority priority;

                if (priorityValue == null || priorityValue.isBlank()) {
                    priority = TaskPriority.MITTEL;
                } else {
                    priority = TaskPriority.valueOf(priorityValue.trim());
                }

                Task task = new Task(
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        priority
                );

                task.setId(resultSet.getInt("id"));

                String reminder = resultSet.getString("reminder");

                if (reminder != null) {
                    task.setReminder(LocalDateTime.parse(reminder));
                }

                task.setCompleted(resultSet.getInt("completed") == 1);

                tasks.add(task);
            }
            return tasks;
        }
    }

    // Aufgabe als erledigt markieren
    public void completeTask(int id) throws SQLException {

        String sql = "UPDATE tasks SET completed = 1 WHERE id = ?";

        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Aufgabe löschen
    public void deleteTask(int id) throws SQLException {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Fällige Erinnerungen laden
    public List<Task> getDueTasks() throws SQLException {

        List<Task> tasks = new ArrayList<>();

        String sql = """
                SELECT id, title, description, priority, reminder, completed
                From tasks
                WHERE reminder IS NOT NULL
                AND reminder <= ?
                AND completed = 0
                AND reminder_sent = 0
                ORDER BY reminder ASC
                """;

        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, LocalDateTime.now().toString());

            try (var resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    TaskPriority priority = TaskPriority.valueOf(
                            resultSet.getString("priority")
                    );

                    Task task = new Task(
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                    priority
                         );

                    task.setId(resultSet.getInt("id"));

                    String reminder = resultSet.getString("reminder");
                    if (reminder != null) {
                        task.setReminder(LocalDateTime.parse(reminder));
                    }
                    task.setCompleted(false);

                    tasks.add(task);
                }
            }
        }
          return tasks;
    }

    //Erinnerung als angezeigt markieren
    public void markReminderAsSent(int id) throws SQLException {

        String sql = """
                UPDATE tasks
                SET reminder_sent = 1
                WHERE id = ?
                """;
        try (Connection connection = databaseService.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            int rowsUpdated = statement.executeUpdate();

            System.out.println(
                    "Reminder-Status aktualisiert. Betroffene Zeilen: " + rowsUpdated);
        }
    }
}
