package org.iu.flashcards.api.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.common.DifficultyAndDuration;
import org.iu.flashcards.api.stack.Stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

  @Column(columnDefinition="TEXT")
  @NotBlank
  private String answer;

  @Column(columnDefinition="TEXT")
  private String hint;

  @OneToMany(mappedBy = "card", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Collection<CardMaturity> maturities;

  private transient List<DifficultyAndDuration> difficultyAndDurations = new ArrayList<>();

  private transient CardMaturity maturity;

  public static Card of(Stack stack, String question, String answer) {
    return new Card(null, stack, UUID.randomUUID().toString(), question, answer, "", new ArrayList<>(), new ArrayList<>(), null);
  }
}
