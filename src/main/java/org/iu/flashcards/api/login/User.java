package org.iu.flashcards.api.login;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
  @Id
  @GeneratedValue(generator = "increment")
  private Long id;

  @Column(nullable = false)
  @NotBlank
  private String uniqueId;

  @Column(nullable = false)
  @NotBlank
  @Size(min = 2)
  private String name;

  @Column(nullable = false)
  @NotBlank
  @Size(min = 2)
  private String mail;

  @Column(nullable = false)
  @NotBlank
  @Size(min = 2)
  private String password;
}
