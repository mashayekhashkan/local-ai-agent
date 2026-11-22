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
        Antworte verständlich, freundlich und präzis
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

// Check whether Java can answer the question itself
    String answer = memoryQueryHandler.handle(userMessage);

// Call Ollama if no direct answer is available
    if (answer == null) {
        answer = ollamaClient.chat(conversationHistory.getMessages());
    }


// Add user message to conversation history
//conversationHistory.add(
//        new ChatMessage("user", userMessage)
//);

// Send conversation history to Ollama
//String answer = ollamaClient.chat(conversationHistory);

// Save the answer
    conversationHistory.addMessage(
            new ChatMessage("assistant", answer)
    );

return answer;
}
}