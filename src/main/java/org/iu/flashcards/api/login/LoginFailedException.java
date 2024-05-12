package org.iu.flashcards.api.login;

public class LoginFailedException extends RuntimeException {
  private String reason;

  public LoginFailedException(String reason) {
    super(reason);
  }

  public String reason() {
    return reason;
  }
}
