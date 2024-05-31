package org.iu.flashcards.api.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.TimeUnit;

public record Duration(@JsonIgnore int amount, @JsonIgnore TimeUnit unit) {

  public String getDisplayName() {
    return amount + " " + unitName(unit, amount);
  }

  public String unitName(TimeUnit unit, int amount) {
    return switch (unit) {
      case DAYS -> "Tag" + (amount > 1 ? "e" : "");
      case HOURS -> "Stunde" + (amount > 1 ? "n" : "");
      case MINUTES -> "Minute" + (amount > 1 ? "n" : "");
      case SECONDS -> "Sekunde" + (amount > 1 ? "n" : "");
      default -> "Unbekannt";
    };
  }

  public long toMillis() {
    return unit.toMillis(amount);
  }
}
