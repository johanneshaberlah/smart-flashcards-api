package org.iu.flashcards.api.stack.assistant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public record AssistantCredentials(@Value("${open-ai-key}") String apiKey, @Value("${open-ai-assistant}") String assistantId) { }
