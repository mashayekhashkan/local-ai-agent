package at.aiagent.model;

import java.util.ArrayList;
import java.util.List;

public class ConversationHistory {

    private final List<ChatMessage> messages;

    public ConversationHistory() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void replaceSystemMessage(String content) {
        if (!messages.isEmpty()
        && messages.get(0).getRole().equals("system")) {
            messages.set(0, new ChatMessage("system", content));
        } else {
            messages.add(0, new ChatMessage("system", content));
        }
    }

    public void limitMessages(int maxMessages) {

        if (maxMessages < 1) {
            throw new IllegalArgumentException(
                    "maxMessage muss mindestendes 1 sein."
            );
        }

        // The system message is always preserved.
        int systemMessageCount = 0;

        if (!messages.isEmpty()
               && messages.get(0).getRole().equals("system")) {
            systemMessageCount = 1;
        }

        // Number of messages to include in addition to the System message must be retained.
        int maxConversationMessages = maxMessages;

        while (messages.size() > systemMessageCount + maxConversationMessages) {
            messages.remove(systemMessageCount);
        }
    }

    public void removeLastMessage() {
        if (!messages.isEmpty()) {
            messages.remove(messages.size() - 1);
        }
    }
}
