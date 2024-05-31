package org.iu.flashcards.api.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.stack.Stack;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @ManyToOne
  @JsonIgnore
  @JoinColumn
  private Stack stack;

  @Column(nullable = false)
  @NotBlank
  private String uniqueId;

  @Column(nullable = false)
  @NotBlank
  private String question;

  @Column(nullable = false)
  @NotBlank
  private String answer;

  private transient CardMaturity maturity;

  public static Card of(Stack stack, String question, String answer) {
    return new Card(null, stack, UUID.randomUUID().toString(), question, answer, null);
  }
}
