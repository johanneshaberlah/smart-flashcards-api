package org.iu.flashcards.api.login;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
@Data
public class UserComponent {
  private User user;
}
