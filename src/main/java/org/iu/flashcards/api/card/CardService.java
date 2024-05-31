package org.iu.flashcards.api.card;

import org.iu.flashcards.api.stack.StackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
  private final StackService stackService;
  private final CardRepository cardRepository;

  @Autowired
  public CardService(StackService stackService, CardRepository cardRepository) {
    this.stackService = stackService;
    this.cardRepository = cardRepository;
  }

  public Card findCard(String stackId, String cardId) {
    var stack = stackService.findStack(stackId);
    return stack.getCards()
      .stream()
      .filter(card -> card.getUniqueId().trim().equals(cardId.trim()))
      .findFirst()
      .orElseThrow(CardNotFoundException::new);
  }

  public Card updateCard(CardContext context) {
    var card = findCard(context.stackId(), context.cardId());
    card.setQuestion(context.question());
    card.setAnswer(context.answer());
    return cardRepository.save(card);
  }

  public Card createCard(CardContext context) {
    var stack = stackService.findStack(context.stackId());
    return cardRepository.save(
      Card.of(stack, context.question(), context.answer())
    );
  }
}
