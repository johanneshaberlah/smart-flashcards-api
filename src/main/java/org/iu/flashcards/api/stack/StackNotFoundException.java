package org.iu.flashcards.api.stack;

import org.iu.flashcards.api.common.ApiError;

public class StackNotFoundException extends RuntimeException {

  public ApiError toApiError() {
    return new ApiError("Stack not found");
  }
}
