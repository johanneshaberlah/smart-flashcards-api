package org.iu.flashcards.api.card;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.iu.flashcards.api.common.ApiError;
import org.iu.flashcards.api.stack.StackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Card", description = "Operations related to Flashcard management")
public class CardController {
  private final CardService cardService;

  @Autowired
  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @Operation(summary = "Retrieve a card by its ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Card found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Card.class))),
    @ApiResponse(responseCode = "404", description = "Card or Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @GetMapping("/stack/{stack}/card/{card}")
  public ResponseEntity<?> readCard(@PathVariable("stack") String stackId, @PathVariable("card") String cardId) {
    try {
      return ResponseEntity.ok(cardService.findCard(stackId, cardId));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cardNotFound.toApiError());
    }
  }

  @Operation(summary = "Update a card")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Card updated",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Card.class))),
    @ApiResponse(responseCode = "404", description = "Card or Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @PutMapping("/stack/{stack}/card/{card}")
  public ResponseEntity<?> updateCard(@RequestBody CardContext cardContext) {
    try {
      return ResponseEntity.ok(cardService.updateCard(cardContext));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cardNotFound.toApiError());
    }
  }

  @Operation(summary = "Delete a card")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Card deleted"),
    @ApiResponse(responseCode = "404", description = "Card or Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @DeleteMapping("/stack/{stack}/card/{card}")
  public ResponseEntity<?> deleteCard(@RequestBody CardContext cardContext) {
    try {
      cardService.deleteCard(cardContext);
      return ResponseEntity.ok().build();
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cardNotFound.toApiError());
    }
  }

  @Operation(summary = "Create a new card")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Card created",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = Card.class))),
    @ApiResponse(responseCode = "404", description = "Stack not found",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ApiError.class)))
  })
  @PostMapping("/card")
  public ResponseEntity<?> createCard(@RequestBody CardContext cardContext) {
    try {
      return ResponseEntity.ok(cardService.createCard(cardContext));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }
}
