package org.iu.flashcards.api.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalControllerAdvice {

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> handle(Exception exception) {
    return ResponseEntity.internalServerError().body(new ApiError(exception.getMessage()));
  }
}
