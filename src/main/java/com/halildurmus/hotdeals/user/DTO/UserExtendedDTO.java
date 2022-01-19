package com.halildurmus.hotdeals.user.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserExtendedDTO {

  @Schema(description = "User ID", example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @Schema(description = "Firebase User ID", example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  private final String uid;

  @Schema(description = "User avatar URL", example = "https://www.gravatar.com/avatar")
  private final String avatar;

  @Schema(description = "User nickname", example = "MrNobody123")
  private final String nickname;

  @Schema(description = "Blocked users", example = "[5fbe790ec6f0b32014074bb2]")
  private final HashSet<String> blockedUsers;

  @Schema(description = "FCM tokens", example = "{\"29ba634d38ac6a1a\": \"dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B\"}")
  private final Map<String, String> fcmTokens;

  @Schema(description = "User createdAt", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
