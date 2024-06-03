package org.iu.flashcards.api.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Login", description = "Operations related to user authentication")
public class LoginController {
  private final LoginService loginService;

  @Autowired
  public LoginController(LoginService loginService) {
    this.loginService = loginService;
  }

  @Operation(summary = "Log in a user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successful",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = LoginResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid credentials",
      content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error",
      content = @Content)
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResult> login(@RequestBody LoginRequestCredentials loginRequestCredentials) {
    try {
      return loginService.performLogin(loginRequestCredentials);
    } catch (LoginFailedException failure) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(null);
    }
  }

  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Registration successful",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = LoginResult.class))),
    @ApiResponse(responseCode = "400", description = "Invalid registration details",
      content = @Content),
    @ApiResponse(responseCode = "500", description = "Internal server error",
      content = @Content)
  })
  @PostMapping("/signup")
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
