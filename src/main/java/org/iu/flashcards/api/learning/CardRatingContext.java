package org.iu.flashcards.api.learning;

public record CardRatingContext(String stackId, String cardId, int difficulty) {
}
