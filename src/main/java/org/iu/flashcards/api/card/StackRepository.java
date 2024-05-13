package org.iu.flashcards.api.card;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@RestResource
@Repository
public interface StackRepository extends JpaRepository<Stack, Long> {
}
