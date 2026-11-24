package at.aiagent.service;

import at.aiagent.OllamaClient;

import java.io.IOException;

public class CodeAssistant {

    private final OllamaClient ollamaClient;

    public CodeAssistant(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String analyzeCode(String code)
        throws IOException, InterruptedException {

        String prompt = """
            Analysiere den folgenden Java-Code.

            Erkläre:
            1. Was macht der Code?
            2. Wie ist der Code aufgebaut?
            3. Welche möglichen Fehler oder Probleme gibt es?
            4. Wie könnte man den Code verbessern?

            Java-Code:
            %s

            Antworte auf Deutsch und verständlich.
            """.formatted(code);

        return ollamaClient.ask(prompt);
    }

    public String fixCode(String code) throws IOException, InterruptedException {

        String prompt = """
            Überprüfe den folgenden Java-Code und korrigiere
            alle erkennbaren Fehler.

            Regeln:
            - Erhalte die ursprüngliche Funktionalität.
            - Ändere nur, was notwendig ist.
            - Erkläre die wichtigsten Änderungen kurz.
            - Gib den vollständigen korrigierten Code aus.

            Java-Code:
            %s
            """.formatted(code);

        return ollamaClient.ask(prompt);
    }

    public String improveCode(String code) throws IOException, InterruptedException {

        String prompt = """
            Verbessere den folgenden Java-Code hinsichtlich:
            - Lesbarkeit
            - Struktur
            - Wartbarkeit
            - Java-Konventionen

            Regeln:
            - Behalte die ursprüngliche Funktionalität bei.
            - Vermeide unnötige Komplexität.
            - Erkläre die wichtigsten Verbesserungen.
            - Gib den vollständigen verbesserten Code aus.

            Java-Code:
            %s
            """.formatted(code);

        return ollamaClient.ask(prompt);
    }

    public String generateCode(String task) throws IOException, InterruptedException {

        String prompt = """
            Erstelle Java-Code für folgende Aufgabe:

            %s

            Regeln:
            - Verwende Java 21.
            - Schreibe vollständigen und ausführbaren Code.
            - Verwende verständliche Namen.
            - Halte den Code möglichst einfach.
            - Erkläre anschließend kurz die Funktionsweise.
            - Antworte auf Deutsch.
            """.formatted(task);

        return ollamaClient.ask(prompt);
    }

    public String solveProgrammingTask(String task) throws IOException, InterruptedException {

        String prompt =  """
            Du bist ein Java-Programmierassistent.

            Deine Aufgabe ist es, kleine Programmieraufgaben
            selbstständig zu verstehen und zu lösen.

            Aufgabenbeschreibung:
            %s

            Anforderungen:
            - Analysiere zuerst die Aufgabenstellung.
            - Entwickle eine passende Java-Lösung.
            - Verwende Java 21.
            - Schreibe vollständigen und verständlichen Code.
            - Berücksichtige mögliche Sonderfälle.
            - Erkläre kurz die Lösung.
            - Antworte auf Deutsch.
            """.formatted(task);

        return ollamaClient.ask(prompt);
    }
}
