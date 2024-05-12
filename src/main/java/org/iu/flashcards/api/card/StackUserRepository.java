package org.iu.flashcards.api.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StackUserRepository extends JpaRepository<StackUser, Long> {
}
