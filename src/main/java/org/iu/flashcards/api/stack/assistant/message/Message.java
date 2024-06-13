package org.iu.flashcards.api.stack.assistant.message;

import java.util.List;

public record Message(String id, List<MessageContent> content) {
}
