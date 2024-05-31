package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.card.Card;
import org.iu.flashcards.api.card.CardNotFoundException;
import org.iu.flashcards.api.stack.StackNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LearningController {
  private final LearningService learningService;

  @Autowired
  public LearningController(LearningService learningService) {
    this.learningService = learningService;
  }

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
