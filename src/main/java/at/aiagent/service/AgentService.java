package at.aiagent.service;

import at.aiagent.OllamaClient;
import at.aiagent.model.Task;
import at.aiagent.model.TaskPriority;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AgentService {

    private final ChatService chatService;
    private final FileService fileService;
    private final DocumentService documentService;
    private final FileOrganizer fileOrganizer;
    private final CodeAssistant codeAssistant;
    private final TaskService taskService;
    private final ReminderService reminderService;

    public AgentService(OllamaClient ollamaClient) throws SQLException {
        this.chatService = new ChatService(ollamaClient);
        this.fileService = new FileService();
        this.documentService = new DocumentService(fileService, ollamaClient);
        this.fileOrganizer = new FileOrganizer();
        this.codeAssistant = new CodeAssistant(ollamaClient);


        DatabaseService databaseService = new DatabaseService();
        databaseService.initializeDatabase();

        this.taskService =  new TaskService(databaseService);
        this.reminderService = new ReminderService(taskService);
    }

    public String processMessage(String userMessage) throws IOException, InterruptedException, SQLException {

        if (isTaskCreationRequest(userMessage)) {

            String title = userMessage.substring(
                    userMessage.indexOf(":") + 1
            ).split("\\R", 2)[0].trim();

            if (title.isBlank()) {
                return "Bitte gib einen Aufgabentitel an.";
            }

            TaskPriority priority = TaskPriority.MITTEL;

            String lowerMessage = userMessage.toLowerCase();

            if (lowerMessage.contains("priorität: hoch")) {
                priority = TaskPriority.HOCH;
            } else if (lowerMessage.contains("priorität: niedrig")) {
                priority = TaskPriority.NIEDRIG;
            }

            LocalDateTime reminder = null;

            if (lowerMessage.contains("erinnerung")) {

                String reminderText = userMessage.substring(
                        lowerMessage.indexOf("erinnerung:") + "erinnerung:".length()
                ).trim();

                try {
                    reminder = LocalDateTime.parse(
                            reminderText.replace(" ", "T")
                    );
                } catch (Exception e) {
                    return "Ungültiges Erinnerungsdatum. Bitte verwende das Format: JJJJ-MM-TT HH:MM";
                }
            }

            Task task = new Task(
                    title,
                    "",
                    priority
            );

            task.setReminder(reminder);

            taskService.addTask(task);

            return "Aufgabe wurde gespeichert: " + title
                    + "\nPriorität: " + priority
                    + (reminder != null ? "\nErinerung: " + reminder : "");
        }

        if (isTaskListRequest(userMessage)) {

            List<Task> tasks = taskService.getAllTasks();

            if (tasks.isEmpty()) {
                return "Du hast noch keine gespeicherten Aufgaben.";
            }

            StringBuilder result = new StringBuilder("Deine Aufgaben:\n");

            for (Task task : tasks) {
                result.append("\nID: ").append(task.getId());
                result.append("\nTitel: ").append(task.getTitle());
                result.append("\nPriorität: ").append(task.getPriority());

                if (task.getReminder() != null) {
                    result.append("\nErinnerung: ").append(task.getReminder());
                }
                result.append("\nStatus: ")
                        .append(task.isCompleted() ? "Erledigt" : "Offen");
                result.append("\n--------------------");
            }

            return result.toString();
        }

        if (isTaskCompletionRequest(userMessage)) {

            String idText = userMessage.substring(
                    userMessage.indexOf(":") + 1
            ).trim();

            try {
                int id = Integer.parseInt(idText);
                taskService.completeTask(id);

                return "Aufgabe " + id + " wurde als erledigt markiert.";
            } catch (NumberFormatException e) {
                return  "Bitte gib eine gültige Aufgabe_ID an.";
            }
        }

        if (isTaskDeletionRequest(userMessage)) {

            String idText = userMessage.substring(
                    userMessage.indexOf(":") + 1
            ).trim();

            try {
                int id = Integer.parseInt(idText);
                taskService.deleteTask(id);

                return "Aufgabe " + id + " wurde gelöscht.";
            } catch (NumberFormatException e) {
                return "Bitte gib eine gültige Aufgaben-ID an.";
            }
        }

        if (isCodeAnalysisRequest(userMessage)) {

            String code = extractCode(userMessage);

            if (code == null || code.isBlank()) {
                return "Bitte füge den Java-Code nach einem Doppelpunkt ein.";
            }
            return codeAssistant.analyzeCode(code);
        }

        if (isCodeFixRequest(userMessage)) {
            String code = extractCode(userMessage);

            if (code == null || code.isBlank()) {
                return "Bitte füge den Java-Code ein.";
            }

            return codeAssistant.fixCode(code);
        }

        if (isCodeImprovementRequest(userMessage)) {
            String code = extractCode(userMessage);

            if (code == null || code.isBlank()) {
                return "Bitte füge den Java-Code ein";
            }

            return codeAssistant.improveCode(code);
        }

        if (isCodeGenerationRequest(userMessage)) {

            String task = extractCode(userMessage);

            if (task == null || task.isBlank()) {
                return "Bitte beschreibe, welchen code ich erstellen soll.";
            }
            return codeAssistant.generateCode(task);
        }

        if (isProgrammingTaskRequest(userMessage)) {

            String task = extractCode(userMessage);

            if (task == null || task.isBlank()) {
                return "Bitte beschreibe die Programmieraufgabe.";
            }
        }

        return chatService.sendMessage(userMessage);
    }

    private boolean isCodeAnalysisRequest(String message) {

        String normalizedMessage = message.toLowerCase();

        return normalizedMessage.contains("analysiere diesen java-code")
                || normalizedMessage.contains("analysiere den java-code")
                || normalizedMessage.contains("analysiere diesen code");
    }

    private boolean isCodeFixRequest(String message) {

        String normalizedMessage = message.toLowerCase();

        return normalizedMessage.contains("korrigiere diesen code")
                || normalizedMessage.contains("korrigiere den java-code")
                || normalizedMessage.contains("behebe die fehler");
    }

    private boolean isCodeImprovementRequest(String message) {

        String normalizedMessage = message.toLowerCase();

        return normalizedMessage.contains("verbessere diesen code")
                || normalizedMessage.contains("verbesser den java-code")
                || normalizedMessage.contains("optimiere diesen code");
    }

    private boolean isCodeGenerationRequest (String message) {

        String normalizedMessage = message.toLowerCase();

        return normalizedMessage.contains("schreibe einen java-code")
                || normalizedMessage.contains("erstelle einen java-code")
                || normalizedMessage.contains("generiere java-code")
                || normalizedMessage.contains("programmiere");
    }

    private boolean isProgrammingTaskRequest(String message) {

        String normalizedMessage = message.toLowerCase();

        return normalizedMessage.contains("löse diese programmieraufgabe")
                || normalizedMessage.contains("löse folgende programmieraufgabe")
                || normalizedMessage.contains("implementiere folgene aufgabe");
    }

    private String extractCode(String message) {

        String[] prefixes = {
                "analysiere diesen java-code",
                "analysiere den java-code",
                "analysiere diesen code",
                "schreibe einen java-code",
                "erstelle einen java-code",
                "generiere java-code",
                "programmiere",
                "löse diese programmieraufgabe",
                "löse folgende programmieraufgabe",
                "programmiere folgende aufgabe",
                "implementiere folgende aufgabe"
        };

        String normalizedMessage = message.toLowerCase();

        for (String prefix : prefixes) {

            if (normalizedMessage.startsWith(prefix)) {

                String code = message.substring(prefix.length()).trim();

                if (code.startsWith(":")) {
                    code = code.substring(1).trim();
                }
                return code;
            }
        }

        return null;

    }

    private boolean isTaskCreationRequest(String message) {
        String text = message.toLowerCase();

        return text.startsWith("aufgabe erstellen:")
                || text.startsWith("neue aufgabe:");
    }

    private boolean isTaskListRequest(String message) {
        String text = message.toLowerCase().trim();

        return text.equals("zeige meine aufgaben")
                || text.equals("aufgaben anzeigen")
                || text.equals("meine aufgaben");
    }

    private boolean isTaskCompletionRequest(String message) {
        String text = message.toLowerCase().trim();

        return text.startsWith("aufgabe erledigen");
    }

    private boolean isTaskDeletionRequest(String message) {
        String text = message.toLowerCase().trim();

        return text.startsWith("aufgabe löschen:")
                || text.startsWith("aufgabe loeschen:");
    }

    public void checkReminders() throws SQLException {
        reminderService.checkReminder();
    }
}
