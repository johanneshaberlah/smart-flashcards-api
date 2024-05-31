package org.iu.flashcards.api.stack;

import org.iu.flashcards.api.login.UserComponent;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StackService {
  private final StackRepository stackRepository;
  private final StackUserRepository stackUserRepository;
  private final ObjectFactory<UserComponent> userComponentFactory;

  public StackService(StackRepository stackRepository, StackUserRepository stackUserRepository, ObjectFactory<UserComponent> userComponentFactory) {
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

  public boolean deleteStack(String uniqueId) {
    var stack = findStack(uniqueId);
    stackRepository.delete(stack);
    return true;
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
