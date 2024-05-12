package org.iu.flashcards.api.login;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginService {
  private final int CRYPTO_LOG_ROUNDS = 12;

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
    return ResponseEntity.ok().body(new LoginResult(user.getName(), user.getUniqueId()));
  }

  public ResponseEntity<LoginResult> performRegistration(RegistrationModel credentials) {
    var user = userRepository.save(
      new User(null, UUID.randomUUID().toString(), credentials.name(), credentials.mail(), BCrypt.hashpw(credentials.password(), BCrypt.gensalt(CRYPTO_LOG_ROUNDS)))
    );
    return ResponseEntity.ok().body(new LoginResult(user.getName(), user.getUniqueId()));
  }
}
