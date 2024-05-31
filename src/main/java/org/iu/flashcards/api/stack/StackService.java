package org.iu.flashcards.api.stack;

import org.iu.flashcards.api.card.Card;
import org.iu.flashcards.api.card.CardContext;
import org.iu.flashcards.api.card.CardRepository;
import org.iu.flashcards.api.login.UserComponent;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StackService {
  private final CardRepository cardRepository;
  private final StackRepository stackRepository;
  private final StackUserRepository stackUserRepository;
  private final ObjectFactory<UserComponent> userComponentFactory;

  public StackService(CardRepository cardRepository, StackRepository stackRepository, StackUserRepository stackUserRepository, ObjectFactory<UserComponent> userComponentFactory) {
    this.cardRepository = cardRepository;
    this.stackRepository = stackRepository;
    this.stackUserRepository = stackUserRepository;
    this.userComponentFactory = userComponentFactory;
  }

  public Stack createStack(StackContext stackContext) {
    var stack = stackRepository.save(Stack.of(stackContext));
    var stackUser = StackUser.of(userComponentFactory.getObject().getUser(), stack);
    stackUserRepository.save(stackUser);
    return stack;
  }

  public Stack findStack(String uniqueId) {
    return userComponentFactory.getObject().getUser().getStackUser()
      .stream()
      .filter(stackUser -> stackUser.getStack().getUniqueId().trim().equals(uniqueId.trim()))
      .peek(stackUser -> stackUser.getStack().getCards().forEach(card -> card.setMaturity(stackUser.getMaturities().stream().filter(maturity -> maturity.getCard().getId().equals(card.getId())).findFirst().orElse(null))))
      .map(StackUser::getStack)
      .findFirst()
      .orElseThrow(StackNotFoundException::new);
  }

  public List<Stack> stacks() {
    return userComponentFactory.getObject().getUser().getStackUser().stream().map(StackUser::getStack).toList();
  }
}
