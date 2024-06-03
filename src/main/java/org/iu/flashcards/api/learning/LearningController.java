package org.iu.flashcards.api.learning;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.iu.flashcards.api.card.Card;
import org.iu.flashcards.api.card.CardNotFoundException;
import org.iu.flashcards.api.common.ApiError;
import org.iu.flashcards.api.stack.StackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@Tag(name = "Learning", description = "Operations related to learning flashcards")
public class LearningController {
  private final LearningService learningService;

  @Autowired
  public LearningController(LearningService learningService) {
    this.learningService = learningService;
  }

  @Operation(summary = "Get the next card for learning")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Next card found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Card.class))),
    @ApiResponse(responseCode = "400", description = "Stack or Card not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @GetMapping("/stack/{stack}/card/next")
  public ResponseEntity<?> nextCard(
    @PathVariable("stack") String stackId,
    @RequestParam(value = "days-ahead", required = false) Optional<Integer> daysAhead
  ) {
    try {
      return ResponseEntity.ok(learningService.nextCard(stackId, daysAhead.orElse(0)));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(400).body(stackNotFound.toApiError());
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(400).body(cardNotFound.toApiError());
    }
  }

  @Operation(summary = "Submit a rating for a card")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Rating submitted successfully"),
    @ApiResponse(responseCode = "400", description = "Stack or Card not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping("/stack/rating")
  public ResponseEntity<?> submitCardRating(@RequestBody CardRatingContext rating) {
    try {
      learningService.submitRating(rating);
      return ResponseEntity.ok().build();
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(400).body(stackNotFound.toApiError());
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(400).body(cardNotFound.toApiError());
    }
  }
}
