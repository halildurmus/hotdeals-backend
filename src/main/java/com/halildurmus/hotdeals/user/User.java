package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
  @Size(min = 5, max = 25)
  private String nickname;

  @URL
  @NotBlank
  private String avatar;

  @Default
  private HashSet<String> blockedUsers = new HashSet<>();

  @Default
  private HashSet<String> favorites = new HashSet<>();

  @Default
  private Map<String, String> fcmTokens = new HashMap<>();

  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
