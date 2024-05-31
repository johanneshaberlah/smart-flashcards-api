package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.card.*;
import org.iu.flashcards.api.common.Difficulty;
import org.iu.flashcards.api.common.DifficultyAndDuration;
import org.iu.flashcards.api.common.Duration;
import org.iu.flashcards.api.login.User;
import org.iu.flashcards.api.login.UserComponent;
import org.iu.flashcards.api.stack.Stack;
import org.iu.flashcards.api.stack.StackService;
import org.iu.flashcards.api.stack.StackUserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class LearningService {
  private final StackUserRepository stackUserRepository;
  private final CardMaturityRepository cardMaturityRepository;
  private final StackService stackService;
  private final CardService cardService;
  private final ObjectFactory<UserComponent> userComponentFactory;

  @Autowired
  public LearningService(
    StackUserRepository stackUserRepository,
    CardMaturityRepository cardMaturityRepository,
    StackService stackService,
    CardService cardService, ObjectFactory<UserComponent> userComponentFactory
  ) {
    this.stackUserRepository = stackUserRepository;
    this.cardMaturityRepository = cardMaturityRepository;
    this.stackService = stackService;
    this.cardService = cardService;
    this.userComponentFactory = userComponentFactory;
  }

  public Card nextCard(String stackId, int daysAhead) {
    var stack = stackService.findStack(stackId);
    var user = userComponentFactory.getObject().getUser();
    return nextCard(stack, user, daysAhead).orElseThrow(CardNotFoundException::new);
  }

  public Optional<Card> nextCard(Stack stack, User user, int daysAhead) {
    return stackUserRepository.findByStackAndUser(stack, user).flatMap(value -> value
      .getMaturities()
      .stream()
      .filter(card -> {
        Instant endOfDay = LocalDate.now().atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant();
        Instant targetInstant = endOfDay.plusMillis(TimeUnit.DAYS.toMillis(daysAhead));
        return card.getMaturity().before(Timestamp.from(targetInstant));
      })
      .sorted(Comparator.comparing(CardMaturity::getMaturity))
      .peek(card -> {
        for (CardDifficulty cardDifficulty : CardDifficulty.values()) {
          card.getCard().getDifficultyAndDurations().add(new DifficultyAndDuration(new Difficulty(cardDifficulty.ordinal(), cardDifficulty.getName(), cardDifficulty.getColor()), incrementDuration(cardDifficulty, card.getLevel())));
        }
      })
      .map(CardMaturity::getCard)

      .findFirst());
  }

  public void submitRating(CardRatingContext ratingContext) {
    var stack = stackService.findStack(ratingContext.stackId());
    var user = userComponentFactory.getObject().getUser();
    var card = cardService.findCard(ratingContext.stackId(), ratingContext.cardId());
    var difficulty = CardDifficulty.values()[ratingContext.difficulty()];
    updateMaturity(stack, user, card, difficulty);
  }

  public void updateMaturity(Stack stack, User user, Card card, CardDifficulty difficulty) {
    stackUserRepository.findByStackAndUser(stack, user).flatMap(stackUser -> stackUser.getMaturities().stream()
      .filter(cardMaturity -> cardMaturity.getCard().getId().equals(card.getId()))
      .findFirst()).ifPresent(cardMaturity -> {
      var increment = incrementDuration(difficulty, cardMaturity.getLevel()).toMillis();
      var base = Timestamp.from(Instant.now());
      var level = cardMaturity.getLevel();
      if (difficulty.equals(CardDifficulty.EASY)) {
        level += 1;
      }
      if (difficulty.equals(CardDifficulty.HARD)) {
        level = Math.max(level - (level > 4 ? level / 2 : 1), 0);
      }
      cardMaturity.setLevel(level);
      cardMaturity.setMaturity(Timestamp.from(Instant.ofEpochMilli(base.getTime() + increment)));

      cardMaturityRepository.save(cardMaturity);
    });
  }

  private Duration incrementDuration(CardDifficulty difficulty, int level) {
    return difficulty.maturityIncrementLevels()[Math.min(level, difficulty.maturityIncrementLevels().length - 1)];
  }
}
