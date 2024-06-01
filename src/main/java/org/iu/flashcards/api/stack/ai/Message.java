package org.iu.flashcards.api.stack.ai;

import java.util.List;

public record Message(String id, List<MessageContent> content) {
}
