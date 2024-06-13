package org.iu.flashcards.api.login;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.SignatureException;
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
      return sessionDecoder.parseClaimsJws(token).getBody();
    } catch (SignatureException signaureInvalid) {
      throw new LoginFailedException("The signature of the token is invalid");
    }  catch (TokenExpiredException tokenExpired) {
      throw new LoginFailedException("The token is expired");
    }
  }
}
