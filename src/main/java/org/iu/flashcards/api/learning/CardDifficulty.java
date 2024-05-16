package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.common.Duration;

import java.util.concurrent.TimeUnit;

public enum CardDifficulty {
  EASY(new Duration[]{ new Duration(1, TimeUnit.MINUTES) }),
  MEDIUM((new Duration[]{ new Duration(1, TimeUnit.HOURS) })),
  HARD((new Duration[]{ new Duration(1, TimeUnit.DAYS) }));

  private final Duration[] maturityIncrementLevels;

  CardDifficulty(Duration[] maturityIncrementLevels) {
    this.maturityIncrementLevels = maturityIncrementLevels;
  }

  public Duration[] maturityIncrementLevels() {
    return maturityIncrementLevels;
  }
}
