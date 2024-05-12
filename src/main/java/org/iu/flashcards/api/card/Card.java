package org.iu.flashcards.api.card;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @ManyToOne
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
}
