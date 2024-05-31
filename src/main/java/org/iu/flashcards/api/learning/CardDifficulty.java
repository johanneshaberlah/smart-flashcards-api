package org.iu.flashcards.api.learning;

import org.iu.flashcards.api.common.Duration;

import java.util.concurrent.TimeUnit;

public enum CardDifficulty {
  EASY("Einfach", "#22C55E", new Duration[]{
    new Duration(10, TimeUnit.MINUTES),
    new Duration(1, TimeUnit.HOURS),
    new Duration(6, TimeUnit.HOURS),
    new Duration(12, TimeUnit.HOURS),
    new Duration(2, TimeUnit.DAYS),
    new Duration(4, TimeUnit.DAYS),
    new Duration(7, TimeUnit.DAYS),
    new Duration(14, TimeUnit.DAYS),
    new Duration(30, TimeUnit.DAYS),

  }),
  MEDIUM("Mittel", "#F97316", (new Duration[]{
    new Duration(2, TimeUnit.MINUTES),
    new Duration(5, TimeUnit.MINUTES),
    new Duration(10, TimeUnit.MINUTES),
    new Duration(30, TimeUnit.MINUTES),
    new Duration(45, TimeUnit.MINUTES),
    new Duration(1, TimeUnit.HOURS),
    new Duration(2, TimeUnit.HOURS),
  })),
  HARD("Schwer", "#EF4444", (new Duration[]{
    new Duration(1, TimeUnit.MINUTES),
    new Duration(1, TimeUnit.MINUTES),
    new Duration(2, TimeUnit.MINUTES),
    new Duration(10, TimeUnit.MINUTES),
  }));

  private final String name;
  private final String color;
  private final Duration[] maturityIncrementLevels;

  CardDifficulty(String name, String color, Duration[] maturityIncrementLevels) {
    this.name = name;
    this.color = color;
    this.maturityIncrementLevels = maturityIncrementLevels;
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
