package org.iu.flashcards.api.stack;

import org.iu.flashcards.api.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StackUserRepository extends JpaRepository<StackUser, Long> {

  Optional<StackUser> findByStackAndUser(Stack stack, User user);


}
