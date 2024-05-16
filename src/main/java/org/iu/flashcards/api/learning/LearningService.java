package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.card.*;
import org.iu.flashcards.api.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

@Service
public class LearningService {
  private final StackUserRepository stackUserRepository;
  private final CardMaturityRepository cardMaturityRepository;

  @Autowired
  public LearningService(StackUserRepository stackUserRepository, CardMaturityRepository cardMaturityRepository) {
    this.stackUserRepository = stackUserRepository;
    this.cardMaturityRepository = cardMaturityRepository;
  }

  public Optional<Card> nextCard(Stack stack, User user) {
    return stackUserRepository.findByStackAndUser(stack, user).flatMap(value -> value
      .getMaturities()
      .stream()
      .sorted(Comparator.comparing(CardMaturity::getMaturity))
      .map(CardMaturity::getCard)
      .findFirst());
  }

  public void updateMaturity(Stack stack, User user, Card card, CardDifficulty difficulty) {
    stackUserRepository.findByStackAndUser(stack, user).flatMap(stackUser -> stackUser.getMaturities().stream()
      .filter(cardMaturity -> cardMaturity.getCard().equals(card))
      .findFirst()).ifPresent(cardMaturity -> {
      var increment = difficulty.maturityIncrementLevels()[cardMaturity.getLevel()].toMillis();
      var base = Timestamp.from(Instant.now());
      cardMaturity.setMaturity(Timestamp.from(Instant.ofEpochMilli(base.getTime() + increment)));
      cardMaturityRepository.save(cardMaturity);
    });
  }
}
