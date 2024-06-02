package org.iu.flashcards.api.card;

import org.iu.flashcards.api.stack.StackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CardController {
  private final CardService cardService;

  @Autowired
  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

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

  @PostMapping("/card")
  public ResponseEntity<?> createCard(@RequestBody CardContext cardContext) {
    try {
      return ResponseEntity.ok(cardService.createCard(cardContext));
    } catch (StackNotFoundException stackNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(stackNotFound.toApiError());
    }
  }
}
