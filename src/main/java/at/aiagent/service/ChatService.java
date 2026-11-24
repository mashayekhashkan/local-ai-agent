package at.aiagent.service;

import at.aiagent.OllamaClient;
import at.aiagent.model.ChatMessage;
import at.aiagent.model.ConversationHistory;
import at.aiagent.model.UserMemory;

import java.io.IOException;

public class ChatService {

    private final OllamaClient ollamaClient;
    private final ConversationHistory conversationHistory;
    private final UserMemory userMemory;
    private final MemoryQueryHandler memoryQueryHandler;

    public ChatService(OllamaClient ollamaClient) {
        this.ollamaClient = ollamaClient;
        this.conversationHistory = new ConversationHistory();
        this.userMemory = new UserMemory();
        this.memoryQueryHandler = new MemoryQueryHandler(userMemory);

        // Create an initial system prompt
        conversationHistory.addMessage(new ChatMessage(
                "system", buildSystemPrompt())
        );
    }

    private String buildSystemPrompt() {
        String prompt = """ 
        Du bist ein hilfreicher KI-Assistent.
                
        Der Mensch, der mit dir spricht, ist der Benutzer.
        Du selbst bist ein KI-Assistent und hast keinen persönlichen Namen.
                
        Der Name des Benutzers und deine Identität sind zwei
        vollständig unterschiedliche Dinge.
                
        Wenn der Benutzer seinen Namen ausdrücklich nennt,
        merke ihn dir für dieses Gespräch.
                
        Wenn der Benutzer fragt, wie er heißt,
        nenne den Namen, den er zuvor angegeben hat.
                
        Wenn du seinen Namen nicht kennst,
        sage ehrlich, dass du ihn nicht kennst.
                
        Verwende niemals den Namen des Benutzers als deinen eigenen Namen.
        Behaupte niemals, dass du der Benutzer bist.
                
        Erfinde keine persönlichen Informationen.
                
        Antworte in der Sprache des Benutzers.
        Antworte verständlich, freundlich und präzise.
        
        Antwortverhalten:
                
        Beantworte die Frage direkt und verständlich.
        Halte einfache Antworten kurz und beschränke dich
        auf die Informationen, nach denen gefragt wurde.
                
        Erstelle Codebeispiele nur, wenn der Benutzer danach fragt.
        Wenn er ein Beispiel verlangt, erkläre es verständlich
        und verwende vollständigen, nachvollziehbaren Code.
                
        Bei komplexen Aufgaben darfst du ausführlicher antworten,
        damit die Lösung vollständig und korrekt ist.
                
        Vermeide unnötige Wiederholungen und zusätzliche Themen.
                
        Ausgabe:
                
        Gib ausschließlich die fertige Antwort aus.
        Zeige keine internen Überlegungen, Planungen
        oder Selbstgespräche.
        """;

        if (userMemory.hasUserName()) {

            prompt += "\nDer Name des Benutzers ist "
                    + userMemory.getUserName()
                    + ". Wenn er nach seinem Namen fragt, nenne diesen Namen.";
        } else {

            prompt += "\nDer Name des Benutzers ist noch unbekannt."
                    + " Erfinde keinen Namen.";

        }

        return prompt;
}

/**
* Die Methode sendMessage() übernimmt drei wichtige Aufgaben:
* Benutzernachricht speichern: Die Frage wird in conversationHistory aufgenommen.
* KI aufrufen: Der gesamte Gesprächsverlauf wird an Ollama übergeben.
* KI-Antwort speichern: Die Antwort wird ebenfalls in der Liste gespeichert.
*/
public String sendMessage(String userMessage)
    throws IOException, InterruptedException {

// Identify users from the message
userMemory.rememberUserNameFromMessage(userMessage);

// Update the system prompt with the current memory
    conversationHistory.replaceSystemMessage(buildSystemPrompt());

// Save user message
    conversationHistory.addMessage(
            new ChatMessage("user", userMessage)
    );

// Limit conversation history before calling the AI
    conversationHistory.limitMessages(10);

// Check whether Java can answer the question itself
    String answer = memoryQueryHandler.handle(userMessage);

// Call Ollama if no direct answer is available
    if (answer == null) {
        try {
            answer = ollamaClient.chat(conversationHistory.getMessages());
        } catch (IOException | InterruptedException e) {
            conversationHistory.removeLastMessage();
            throw e;
        }

    }

// Save the answer
    conversationHistory.addMessage(
            new ChatMessage("assistant", answer)
    );
// Limit conversation history
    conversationHistory.limitMessages(10);

return answer;
}
}