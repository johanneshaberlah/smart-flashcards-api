package org.iu.flashcards.api.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CardController {
  private final CardService cardService;

  @Autowired
  public CardController(CardService cardService) {
    this.cardService = cardService;
  }

  @PostMapping("/card")
  public ResponseEntity<?> card(@RequestBody CardContext cardContext) {
    try {
      return ResponseEntity.ok(cardService.createCard(cardContext));
    } catch (CardNotFoundException cardNotFound) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cardNotFound.toApiError());
    }
  }
}
