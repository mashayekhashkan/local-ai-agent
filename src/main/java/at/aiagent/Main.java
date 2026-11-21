package at.aiagent;

import at.aiagent.service.ChatService;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        OllamaClient ollamaClient = new OllamaClient();
        ChatService chatService = new ChatService(ollamaClient);

        System.out.println("==================");
        System.out.println(" LOCAL AI AGENT");
        System.out.println("==================");

        System.out.println("Application: Local AI Agent");
        System.out.println("Language: Java 21");
        System.out.println("LLM Runtime: Ollama");
        System.out.println("Status: Initialized");

        while (true) {
            System.out.print("\nYou: ");
            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                System.out.println("AI Agent: Goodbye!");
                break;
            }

            if (question.isBlank()) {
                continue;
            }

            try {
                System.out.println("\nAI Agent: Thinking...");

                String answer = chatService.sendMessage(question);

                System.out.println("\nAI Agent: " + answer);
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
        scanner.close();
    }
}