package com.halildurmus.hotdeals.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UserPostDTO {

  @Schema(description = "Firebase User ID", example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  @NotBlank
  private final String uid;

  @Schema(description = "User avatar URL", example = "https://www.gravatar.com/avatar")
  @URL
  @NotBlank
  private final String avatar;

  @Schema(description = "User email address", example = "halildurmus97@gmail.com")
  @Email
  @NotBlank
  private final String email;
}
