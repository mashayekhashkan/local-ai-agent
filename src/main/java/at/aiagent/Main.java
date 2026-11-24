package at.aiagent;

import at.aiagent.model.UserMemory;
import at.aiagent.service.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        OllamaClient ollamaClient = new OllamaClient();
        AgentService agentService = new AgentService(ollamaClient);
        FileService fileService = new FileService();
        DocumentService documentService = new DocumentService(fileService,ollamaClient);
        FileOrganizer fileOrganizer = new FileOrganizer();
        CodeAssistant codeAssistant = new CodeAssistant(ollamaClient);
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                agentService.checkReminders();
            } catch (SQLException e) {
                System.out.println(
                        "Fehler bei der Erinnerung: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);



        System.out.println("==================");
        System.out.println(" LOCAL AI AGENT");
        System.out.println("==================");

        System.out.println("Application: Local AI Agent");
        System.out.println("Language: Java 21");
        System.out.println("LLM Runtime: Ollama");
        System.out.println("Status: Initialized");

//        try {
//            List<String> files = fileService.listFiles(
//                    Path.of("C:\\Users\\Verkäufer\\Documents")
//            );
//
//            System.out.println("\nDateien im Documents-Ordner:");
//
//            for (String file : files) {
//                System.out.println(file);
//            }
//        } catch (IOException e) {
//            System.out.println("Fehler: " + e.getMessage());
//        }
//
//        try {
//            String content = fileService.readTextFile(
//                    Path.of("C:\\Users\\Verkäufer\\Documents\\test.txt")
//            );
//
//            System.out.println("\nInhalt der Testdatei:");
//            System.out.println(content);
//        } catch (IOException e) {
//            System.out.println("Fehler beim Lesen" + e.getMessage());
//        }
//
//        try {
//            String content = fileService.readPdfFile(
//                    Path.of("C:\\Users\\Verkäufer\\Documents\\Lebenslauf.pdf")
//            );
//
//            System.out.println("\nInhalt der PDF-Datei:");
//            System.out.println(content);
//        } catch (IOException e) {
//            System.out.println("Fehler beim Lesen der PDF: " + e.getMessage());
//        }
//
//        try {
//            Path textFile = Path.of(
//                    "C:\\Users\\Verkäufer\\Documents\\test.txt"
//            );
//
//            Path pdfFile = Path.of(
//                    "C:\\Users\\Verkäufer\\Documents\\Lebenslauf.pdf"
//            );
//
//            System.out.println("\n--- TEXTDATEI ---");
//            System.out.println(fileService.readFile(textFile));
//
//            System.out.println("\n--- PDF-DATEI ---");
//            System.out.println(fileService.readFile(pdfFile));
//        } catch (IOException e) {
//            System.out.println("Fehler: " + e.getMessage());
//        }
//
//        try {
//            {
//                String summary = documentService.summarizeDocument(
//                        Path.of("C:\\Users\\Verkäufer\\Documents\\Lebenslauf.pdf")
//                );
//
//                System.out.println("\n--- DOKUMENT-ZUSAMMENFASSUNG ---");
//                System.out.println(summary);
//            }
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Dokumentenanalyse: " + e.getMessage());
//        }
//
//        try {
//            String information = documentService.extractInformation(
//                    Path.of("C:\\Users\\Verkäufer\\Documents\\Lebenslauf.pdf"),
//                    "Welche Ausbildung hat die Person?"
//            );
//
//            System.out.println("\n--- INFORMATIONSEXTRAKTION ---");
//            System.out.println(information);
//
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Informationsextraktion: "
//                    + e.getMessage());
//        }
//
//        try {
//            Map<String, List<Path>> organizedFiles = fileOrganizer.organizeByType(
//                    Path.of("C:\\Users\\Verkäufer\\Documents")
//            );
//
//            organizedFiles.forEach((category, files) -> {
//                System.out.println("\n" + category + ":");
//
//                files.forEach(file ->
//                        System.out.println("  " + file.getFileName()));
//            });
//        } catch (IOException e) {
//            System.out.println("Fehler beim Sortieren der Dateien: "
//                    + e.getMessage());
//        }

//        String javaCode = """
//        public class Calculator {
//
//            public int add(int a, int b) {
//                return a - b;
//            }
//        }
//        """;

//        try {
//            String analysis = codeAssistant.analyzeCode(javaCode);
//
//            System.out.println("\n--- CODE-ANALYSE ---");
//            System.out.println(analysis);
//        } catch (IOException | InterruptedException e) {
//
//                System.out.println("Fehler bei der Code-Anakyse: "
//                + e.getMessage());
//        }

//        try {
//            String correctedCode = codeAssistant.fixCode(javaCode);
//            System.out.println(correctedCode);
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Code-Korrektur"
//             + e.getMessage());
//        }
//
//        try {
//            String improvedCode = codeAssistant.improveCode(javaCode);
//
//            System.out.println("\n--- CODE-VERBESSERUNG ---");
//            System.out.println(improvedCode);
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Code-Verbesserung: "
//            + e.getMessage());
//        }

//        try {
//            String generatedCode = codeAssistant.generateCode(
//                    "Erstelle eine Java-Klasse namens Calculator mit "
//                            + "drei Methoden: add, subtract und multiply. "
//                            + "Jede Methode soll zwei int-Werte erhalten "
//                            + "und das Ergebnis zurückgeben."
//            );
//
//            System.out.println("\n--- CODE-GENERIERUNG ---");
//            System.out.println(generatedCode);
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Code-Generierung" + e.getMessage());
//        }

//        try {
//            String solution = codeAssistant.solveProgrammingTask(
//                    "Schreibe eine Java-Methode, die eine Liste von "
//                            + "Ganzzahlen erhält und eine neue Liste mit "
//                            + "allen geraden Zahlen zurückgibt. "
//                            + "Berücksichtige auch eine leere Liste."
//            );
//
//            System.out.println("\n--- PROGRAMMIERAUFGABE ---");
//            System.out.println(solution);
//        } catch (IOException | InterruptedException e) {
//            System.out.println("Fehler bei der Programmieraufgabe: " + e.getMessage());
//        }

        while (true) {
            System.out.print("\nYou: ");
            StringBuilder input = new StringBuilder();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.trim().equalsIgnoreCase("END")) {
                    break;
                }

                if (input.length() > 0) {
                    input.append("\n");
                }

                input.append(line);
            }

            String question = input.toString();

            if (question.equalsIgnoreCase("exit")) {
                System.out.println("AI Agent: Goodbye!");
                break;
            }

            if (question.isBlank()) {
                continue;
            }

            try {
                System.out.println("\nAI Agent: Thinking...");

                String answer = agentService.processMessage(question);

                System.out.println("\nAI Agent: " + answer);
            } catch (java.net.ConnectException e) {
                System.out.println("\nAI Agent: Der KI-Dienst ist momentan nicht erreichbar. Bitte überprüfe, ob Ollama läuft.");
            } catch (Exception e) {
                System.out.println("\nAI Agent: Ein unerwarteter Fehler ist aufgetreten: "
                        + e.getMessage());
            }
        }
        scanner.close();
    }
}