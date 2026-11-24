package at.aiagent.service;

import at.aiagent.OllamaClient;

import java.io.IOException;
import java.nio.file.Path;

public class DocumentService {

    private final FileService fileService;
    private final OllamaClient ollamaClient;

    public DocumentService(FileService fileService, OllamaClient ollamaClient) {
        this.fileService = fileService;
        this.ollamaClient = ollamaClient;
    }

    public String summarizeDocument(Path file)  throws IOException, InterruptedException {

        String documentContent = fileService.readFile(file);

            String prompt = """
                Fasse das folgende Dokument verständlich und
                übersichtlich zusammen.

                Dokumentinhalt:
                %s
                """.formatted(documentContent);

            return ollamaClient.ask(prompt);

    }

    public String extractInformation(Path file, String requestedInformation)
            throws IOException, InterruptedException {

        String documentContent = fileService.readFile(file);

        String prompt = """
            Extrahiere die angeforderten Informationen
            aus dem folgenden Dokument.

            Gesuchte Informationen:
            %s

            Dokumentinhalt:
            %s

            Regeln:
            - Verwende ausschließlich Informationen aus dem Dokument.
            - Erfinde keine Angaben.
            - Wenn eine Information fehlt, sage das ausdrücklich.
            - Antworte übersichtlich und präzise.
            """.formatted(requestedInformation, documentContent);

        return ollamaClient.ask(prompt);
    }
}
