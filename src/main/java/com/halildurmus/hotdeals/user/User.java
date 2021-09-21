package com.halildurmus.hotdeals.user;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
@Data
@NoArgsConstructor
public class User implements Serializable {

  private static final long serialVersionUID = 1234567L;

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
  @Size(min = 5, max = 20)
  private String nickname;

  @URL
  @NotBlank
  private String avatar;

  private List<String> blockedUsers = new ArrayList<>();

  private List<String> fcmTokens = new ArrayList<>();

  private Map<String, Boolean> favorites = new HashMap<>();

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
