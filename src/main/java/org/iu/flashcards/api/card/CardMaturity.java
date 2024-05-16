package org.iu.flashcards.api.card;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.login.User;

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
  @JoinColumn
  private StackUser user;

  @ManyToOne
  @JoinColumn
  private Card card;

  @Basic
  private Timestamp maturity;

  @Column
  @Min(0)
  @Max(10)
  private int level;
}
