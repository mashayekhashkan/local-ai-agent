package at.aiagent;

import at.aiagent.model.ChatMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OllamaClient {

    private static final String API_URL = "http://localhost:11434/api/chat";

    private static final String MODEL = "qwen3:1.7b";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OllamaClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String ask(String question) throws IOException, InterruptedException {

        // Create JSON request
        ObjectNode requestBody = objectMapper.createObjectNode();

        requestBody.put("model", MODEL);
        requestBody.put("stream", false);
        requestBody.put("think", false);

        ObjectNode options = requestBody.putObject("options");
        options.put("num_predict", 250);

        // Add messages array
        ArrayNode messages = requestBody.putArray("messages");

        ObjectNode message = messages.addObject();
        message.put("role", "user");
        message.put("content", question);


        // Convert Java object to JSON
        String json = objectMapper.writeValueAsString(requestBody);
        System.out.println("DEBUG - Request JSON:");
        System.out.println(json);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        // Send request to ollama
        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );
        System.out.println("DEBUG - Ollama response:");
        System.out.println(response.body());

        // Check HTTP status
        if (response.statusCode() != 200) {
            throw new IOException(
                    "Ollama API error: " + response.body()
            );
        }

        // Read AI response
        JsonNode responseJson = objectMapper.readTree(response.body());

       String answer = responseJson
               .path("message")
               .path("content")
               .asText();

       if (answer.isBlank()) {
           return "Keine Antwort von KI-Modell erhalten";
       }

       return answer;
    }

    public String chat(List<ChatMessage> conversationHistory)
        throws IOException, InterruptedException {

        ObjectNode requestBody = objectMapper.createObjectNode();

        requestBody.put("model", MODEL);
        requestBody.put("stream", false);
        requestBody.put("think", false);

        ObjectNode options = requestBody.putObject("options");
        options.put("num_predict", 250);

        ArrayNode messages = requestBody.putArray("messages");

        for (ChatMessage chatMessage : conversationHistory) {
            ObjectNode message = messages.addObject();
            message.put("role", chatMessage.getRole());
            message.put("content", chatMessage.getContent());
        }

        String json = objectMapper.writeValueAsString(requestBody);

        System.out.println("DEBUG - Request JSON:");
        System.out.println(json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        json, StandardCharsets.UTF_8
                ))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
        );

        if (response.statusCode() != 200) {
            throw new  IOException("Ollama AOI erroe: " + response.body());
        }
        System.out.println("DEBUG - Ollama response:");
        System.out.println(response.body());

        JsonNode responseJson = objectMapper.readTree(response.body());

        String answer = responseJson
                .path("message")
                .path("content")
                .asText();

        if (answer.isBlank()) {
            throw new IOException("Keine Antwort vom KI-Model erhalten.");
        }

        return answer;
    }
}
