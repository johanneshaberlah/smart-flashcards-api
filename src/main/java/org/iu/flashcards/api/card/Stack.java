package org.iu.flashcards.api.card;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stack {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @Column(nullable = false)
  @NotBlank
  private String uniqueId;

  @Column(nullable = false)
  @NotBlank
  private String name;

  @Column(nullable = false)
  @NotBlank
  private String color;

  @OneToMany(mappedBy = "stack", fetch = FetchType.EAGER)
  private Collection<StackUser> user;

  @OneToMany(mappedBy = "stack", fetch = FetchType.EAGER)
  private Collection<Card> cards;
}
