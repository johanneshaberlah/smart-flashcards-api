package org.iu.flashcards.api.login;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenValidator {
  private final JwtParser sessionDecoder;

  @Autowired
  public TokenValidator(JwtParser sessionDecoder) {
    this.sessionDecoder = sessionDecoder;
  }

  public Claims validate(String token) {
    try {
      var claims = sessionDecoder.parseClaimsJws(token);
      var expirationDate = claims.getBody().getExpiration();
      if (expirationDate.before(new Date())) {
        throw new LoginFailedException("Session is invalid");
      }
      return claims.getBody();
    } catch (Throwable failure) {
      throw new LoginFailedException("Session is invalid");
    }
  }
}
