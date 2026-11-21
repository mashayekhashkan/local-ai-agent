package at.aiagent.service;

import at.aiagent.OllamaClient;
import at.aiagent.model.ChatMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatService {

    private final OllamaClient ollamaClient;
    private final List<ChatMessage> conversationHistory;

    public ChatService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
        this.conversationHistory = new ArrayList<>();
    }

    /**
     * Die Methode sendMessage() übernimmt drei wichtige Aufgaben:
     * Benutzernachricht speichern: Die Frage wird in conversationHistory aufgenommen.
     * KI aufrufen: Der gesamte Gesprächsverlauf wird an Ollama übergeben.
     * KI-Antwort speichern: Die Antwort wird ebenfalls in der Liste gespeichert.
     */
    public String sendMessage(String userMessage)
            throws IOException, InterruptedException {
        // Add user message to conversation history
        conversationHistory.add(
                new ChatMessage("user", userMessage)
        );

        // Send conversation history to Ollama
        String answer = ollamaClient.chat(conversationHistory);

        // Add AI response to conversation history
        conversationHistory.add(
                new ChatMessage("assistant", answer)
        );

        return answer;
    }
}
