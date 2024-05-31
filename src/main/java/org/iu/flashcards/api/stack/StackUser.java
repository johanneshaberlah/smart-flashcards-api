package org.iu.flashcards.api.stack;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.card.CardMaturity;
import org.iu.flashcards.api.login.User;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StackUser {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @ManyToOne
  @JoinColumn
  private User user;

  @ManyToOne
  @JoinColumn
  private Stack stack;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private Collection<CardMaturity> maturities;

  public static StackUser of(User user, Stack stack) {
    return new StackUser(null, user, stack, new ArrayList<>());
  }
}
