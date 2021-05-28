package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@TypeAlias("user")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class User {

  @Id
  private String id;

  @Indexed(unique = true)
  @NotBlank
  private String uid;

  @Indexed(unique = true)
  @Email
  @NotBlank
  private String email;

  @Indexed(unique = true)
  @NotBlank
  private String nickname;

  @URL
  @NotNull
  private String avatar;

  private List<String> blockedUsers = new ArrayList<>();

  @CreatedDate
  @Setter(AccessLevel.NONE)
  private Instant createdAt;

  @LastModifiedDate
  @Setter(AccessLevel.NONE)
  private Instant updatedAt;

  public User(String id) {
    this.id = id;
  }

  public User(String uid, String email, String nickname, String avatar) {
    this.uid = uid;
    this.email = email;
    this.nickname = nickname;
    this.avatar = avatar;
  }

}
