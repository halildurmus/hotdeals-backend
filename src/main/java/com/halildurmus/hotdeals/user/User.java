package com.halildurmus.hotdeals.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "User")
@Document(collection = "users")
@TypeAlias("user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

  private static final long serialVersionUID = 1234567L;

  @Schema(description = "User ID", example = "5fbe790ec6f0b32014074bb1")
  @Id
  private String id;

  @Schema(description = "Firebase User ID", example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  @Indexed(unique = true)
  @NotBlank
  private String uid;

  @Schema(description = "User email address", example = "halildurmus97@gmail.com")
  @Indexed(unique = true)
  @Email
  @NotBlank
  private String email;

  @Schema(description = "User nickname", example = "MrNobody123")
  @Indexed(unique = true)
  @NotBlank
  @Size(min = 5, max = 25)
  private String nickname;

  @Schema(description = "User avatar URL", example = "https://www.gravatar.com/avatar")
  @URL
  @NotBlank
  private String avatar;

  @Schema(description = "Blocked users", example = "[5fbe790ec6f0b32014074bb2]")
  @Default
  private HashSet<String> blockedUsers = new HashSet<>();

  @Schema(description = "Favorited deals", example = "[5fbe790ec6f0b32014074bb4]")
  @Default
  private HashSet<String> favorites = new HashSet<>();

  @Schema(description = "FCM tokens", example = "{\"29ba634d38ac6a1a\": \"dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B\"}")
  @Default
  private Map<String, String> fcmTokens = new HashMap<>();

  @Schema(description = "User createdAt", example = "2021-06-30T16:36:59.713Z")
  @CreatedDate
  // See https://github.com/spring-projects/spring-data-rest/issues/1565
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant createdAt;

  @Schema(description = "User updatedAt", example = "2021-06-30T16:36:59.713Z")
  @LastModifiedDate
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Instant updatedAt;

}
