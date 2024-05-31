package org.iu.flashcards.api.card;

import org.iu.flashcards.api.stack.StackService;
import org.springframework.beans.factory.annotation.Autowired;

public class CardService {
  private final StackService stackService;
  private final CardRepository cardRepository;

  @Autowired
  public CardService(StackService stackService, CardRepository cardRepository) {
    this.stackService = stackService;
    this.cardRepository = cardRepository;
  }

  public Card createCard(CardContext context) {
    var stack = stackService.findStack(context.stackId());
    return cardRepository.save(
      Card.of(stack, context.question(), context.answer())
    );
  }
}
