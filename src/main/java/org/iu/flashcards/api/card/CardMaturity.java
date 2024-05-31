package org.iu.flashcards.api.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.stack.StackUser;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardMaturity {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @ManyToOne
  @JsonIgnore
  @JoinColumn
  private StackUser user;

  @ManyToOne
  @JsonIgnore
  @JoinColumn
  private Card card;

  @Basic
  private Timestamp maturity;

  @Column
  @Min(0)
  @Max(10)
  private int level;

  public static CardMaturity initialMaturity(StackUser user, Card card) {
    return new CardMaturity(null, user, card, new Timestamp(System.currentTimeMillis()), 0);
  }
}
