package org.iu.flashcards.api.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginService {
  private static final int CRYPTO_LOG_ROUNDS = 12;
  private static final long SESSION_DURATION = 1000 * 604800;
  public static final String JWT_SECRET = "3776b7f2-474f-4236-9b88-e70f13e1063e";

  private final UserRepository userRepository;

  @Autowired
  public LoginService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public ResponseEntity<LoginResult> performLogin(LoginRequestCredentials credentials) {
    var user = userRepository.findByMail(credentials.mail()).orElseThrow(() -> new LoginFailedException("USER_NOT_FOUND"));
    if (!BCrypt.checkpw(credentials.password(), user.getPassword())) {
      throw new LoginFailedException("WRONG_PASSWORD");
    }
    return ResponseEntity.ok().body(new LoginResult(user.getName(), beginSession(user)));
  }

  public ResponseEntity<LoginResult> performRegistration(RegistrationModel credentials) {
    if (userRepository.findByMail(credentials.mail()).isPresent()) {
      throw new LoginFailedException("USER_ALREADY_EXISTS");
    }
    var user = userRepository.save(
      User.create(credentials.name(), credentials.mail(), BCrypt.hashpw(credentials.password(), BCrypt.gensalt(CRYPTO_LOG_ROUNDS)))
    );
    return ResponseEntity.ok().body(new LoginResult(user.getName(), beginSession(user)));
  }

  private String beginSession(User user) {
    return JWT.create()
      .withClaim("uniqueId", user.getUniqueId())
      .withExpiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + SESSION_DURATION))
      .sign(Algorithm.HMAC256(JWT_SECRET));
  }
}
