package at.aiagent.service;

import at.aiagent.model.UserMemory;

import java.util.Locale;

public class MemoryQueryHandler {

    private final UserMemory userMemory;

    public MemoryQueryHandler(UserMemory userMemory) {
        this.userMemory = userMemory;
    }

    public String handle(String userMessage) {

        String question = userMessage
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[?!.,]+$", "");

        // Ask for the user's name
        if (question.equals("wie heiße ich")
            || question.equals("was ist mein name")
                || question.equals("wie ist mein name")) {

            if (userMemory.hasUserName()) {
                return "Du heißt " + userMemory.getUserName() + ".";
            }
            return "Du hast mir deinen Namen noch nicht genannt.";
        }

        // Ask for the assistant's identity
        if (question.equals("wie heiß du")
        || question.equals("wer bist du")
        || question.equals("was bist du")) {
            return "Ich bin dein KI-Assistent";
        }

        // No recognized memory-related question
        return null;
    }
}
