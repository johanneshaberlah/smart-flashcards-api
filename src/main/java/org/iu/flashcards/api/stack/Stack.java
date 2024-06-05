package org.iu.flashcards.api.stack;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.card.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
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

  @OneToMany(mappedBy = "stack", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Collection<StackUser> user;

  @OneToMany(mappedBy = "stack", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<Card> cards;

  public static Stack of(StackContext context) {
    return new Stack(
      null, UUID.randomUUID().toString(), context.name(), context.color(), new ArrayList<>(), new ArrayList<>()
    );
  }
}
