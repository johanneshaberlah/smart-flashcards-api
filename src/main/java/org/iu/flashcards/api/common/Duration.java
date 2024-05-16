package org.iu.flashcards.api.common;

import java.util.concurrent.TimeUnit;

public record Duration(int amount, TimeUnit unit) {

  public long toMillis() {
    return unit.toMillis(amount);
  }
}
