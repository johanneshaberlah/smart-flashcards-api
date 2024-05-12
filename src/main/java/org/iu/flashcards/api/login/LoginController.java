package org.iu.flashcards.api.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  private final LoginService loginService;

  @Autowired
  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @RequestMapping("/login")
  public ResponseEntity<LoginResult> login(@RequestBody LoginRequestCredentials loginRequestCredentials) {
    try {
      return loginService.performLogin(loginRequestCredentials);
    } catch (LoginFailedException failure) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(null);
    }
  }

  @RequestMapping("/signup")
  public ResponseEntity<LoginResult> signup(@RequestBody RegistrationModel registrationModel) {
    try {
      return loginService.performRegistration(registrationModel);
    } catch (LoginFailedException failure) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().body(null);
    }
  }
}
