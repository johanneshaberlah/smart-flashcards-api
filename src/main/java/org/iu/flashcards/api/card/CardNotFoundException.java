package org.iu.flashcards.api.card;

import org.iu.flashcards.api.common.ApiError;

public class CardNotFoundException extends RuntimeException {

  public ApiError toApiError() {
    return new ApiError("Card not found");
  }
}
