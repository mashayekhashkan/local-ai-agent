package at.aiagent.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Path;

public class DatabaseService {

    private static final String DATABASE_URL = "jdbc:sqlite:agent.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public void initializeDatabase() throws SQLException {

        System.out.println("Database path: "
                + Path.of("agent.db").toAbsolutePath());

        String sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                priority TEXT NOT NULL,
                reminder TEXT,
                completed INTEGER NOT NULL DEFAULT 0,
                reminder_sent INTEGER NOT NULL DEFAULT 0
            )
            """;

        try (Connection connection = connect();
             var statement = connection.createStatement()) {

            statement.execute(sql);

            statement.execute("""
                ALTER TABLE tasks
                ADD COLUMN reminder_sent INTEGER NOT NULL DEFAULT 0
                """);
        } catch (SQLException e) {

            if (!e.getMessage().contains("duplicate column name")) {
                throw e;
            }
        }
    }
}
