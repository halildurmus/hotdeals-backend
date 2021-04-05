package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@TypeAlias("user")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
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
  @NotBlank
  private String avatar;

  public User(String uid, String email, String nickname, String avatar) {
    this.uid = uid;
    this.email = email;
    this.nickname = nickname;
    this.avatar = avatar;
  }

}
