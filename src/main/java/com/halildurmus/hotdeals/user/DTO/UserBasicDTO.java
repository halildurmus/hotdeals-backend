package com.halildurmus.hotdeals.user.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Schema(name = "UserBasicDTO")
@Data
@Builder
public class UserBasicDTO {

  @Schema(description = "User ID", example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @Schema(description = "Firebase User ID", example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  private final String uid;

  @Schema(description = "User avatar URL", example = "https://www.gravatar.com/avatar")
  private final String avatar;

  @Schema(description = "User nickname", example = "MrNobody123")
  private final String nickname;

  @Schema(description = "User createdAt", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
