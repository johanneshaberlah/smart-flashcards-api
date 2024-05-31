package org.iu.flashcards.api.login;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iu.flashcards.api.stack.StackUser;

import java.util.Collection;
import java.util.UUID;

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

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private Collection<StackUser> stackUser;

  public static User create(String name, String mail, String password) {
    User user = new User();
    user.setUniqueId(UUID.randomUUID().toString());
    user.setName(name);
    user.setMail(mail);
    user.setPassword(password);
    return user;
  }
}
