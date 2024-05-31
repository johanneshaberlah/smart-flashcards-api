package org.iu.flashcards.api.card;

public record CardContext(String stackId, String cardId, String question, String answer) {
}
