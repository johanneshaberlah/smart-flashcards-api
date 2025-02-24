package org.iu.flashcards.api.login;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByMail(String mail);
  Optional<User> findByUniqueId(String uniqueId);

}
