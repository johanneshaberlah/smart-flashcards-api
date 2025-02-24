package org.iu.flashcards.api.card;

import org.iu.flashcards.api.stack.StackNotFoundException;
import org.iu.flashcards.api.stack.StackService;
import org.iu.flashcards.api.stack.assistant.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
public class CardService {
  private final ExecutorService hintThreadPool = Executors.newCachedThreadPool();
  private final List<Consumer<Card>> cardCreationListener = new ArrayList<>();

  private final StackService stackService;
  private final CardRepository cardRepository;
  private final CardMaturityRepository cardMaturityRepository;

  @Autowired
  public CardService(StackService stackService, CardRepository cardRepository, CardMaturityRepository cardMaturityRepository) {
    this.stackService = stackService;
    this.cardRepository = cardRepository;
    this.cardMaturityRepository = cardMaturityRepository;
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

  public void deleteCard(CardContext context) {
    var card = findCard(context.stackId(), context.cardId());
    cardRepository.delete(card);
  }

  public Card createCard(CardContext context) {
    var stack = stackService.findStack(context.stackId());
    var stackUser = stackService.findStackUser(context.stackId()).orElseThrow(StackNotFoundException::new);
    var card = cardRepository.save(
      Card.of(stack, context.question(), context.answer())
    );
    cardMaturityRepository.save(CardMaturity.initialMaturity(stackUser, card));
    hintThreadPool.execute(() -> cardCreationListener.forEach(consumer -> consumer.accept(card)));
    return card;
  }

  public void save(Card card) {
    cardRepository.save(card);
  }

  public void registerCardCreationListener(Consumer<Card> cardCreationListener) {
    this.cardCreationListener.add(cardCreationListener);
  }

  public List<Card> findAll() {
    return cardRepository.findAll();
  }
}
