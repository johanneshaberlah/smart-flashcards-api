package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.common.Duration;

import java.util.concurrent.TimeUnit;

public enum CardDifficulty {
  EASY("Einfach", "#22C55E", new Duration[]{
    createDuration(10, TimeUnit.MINUTES),
    createDuration(1, TimeUnit.HOURS),
    createDuration(6, TimeUnit.HOURS),
    createDuration(12, TimeUnit.HOURS),
    createDuration(2, TimeUnit.DAYS),
    createDuration(4, TimeUnit.DAYS),
    createDuration(7, TimeUnit.DAYS),
    createDuration(14, TimeUnit.DAYS),
    createDuration(30, TimeUnit.DAYS),

  }),
  MEDIUM("Mittel", "#F97316", (new Duration[]{
    createDuration(2, TimeUnit.MINUTES),
    createDuration(5, TimeUnit.MINUTES),
    createDuration(10, TimeUnit.MINUTES),
    createDuration(30, TimeUnit.MINUTES),
    createDuration(45, TimeUnit.MINUTES),
    createDuration(1, TimeUnit.HOURS),
    createDuration(2, TimeUnit.HOURS),
  })),
  HARD("Schwer", "#EF4444", (new Duration[]{
    createDuration(1, TimeUnit.MINUTES),
    createDuration(1, TimeUnit.MINUTES),
    createDuration(2, TimeUnit.MINUTES),
    createDuration(10, TimeUnit.MINUTES),
  }));

  private final String name;
  private final String color;
  private final Duration[] maturityIncrementLevels;

  CardDifficulty(String name, String color, Duration[] maturityIncrementLevels) {
    this.name = name;
    this.color = color;
    this.maturityIncrementLevels = maturityIncrementLevels;
  }

  private static Duration createDuration(int amount, TimeUnit unit) {
    return new Duration(amount, unit);
  }

  public String getColor() {
    return color;
  }

  public String getName() {
    return name;
  }

  public Duration[] maturityIncrementLevels() {
    return maturityIncrementLevels;
  }
}
