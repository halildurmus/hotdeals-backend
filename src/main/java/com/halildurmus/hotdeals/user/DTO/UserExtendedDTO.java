package com.halildurmus.hotdeals.user.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@ApiModel("UserExtendedDTO")
@Data
@Builder
public class UserExtendedDTO {

  @ApiModelProperty(value = "User ID", position = 1, example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @ApiModelProperty(value = "Firebase User ID", position = 2, example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  private final String uid;

  @ApiModelProperty(value = "User avatar URL", position = 3, example = "https://www.gravatar.com/avatar")
  private final String avatar;

  @ApiModelProperty(value = "User nickname", position = 4, example = "MrNobody123")
  private final String nickname;

  @ApiModelProperty(value = "Blocked users", position = 5, example = "[5fbe790ec6f0b32014074bb2]")
  private final HashSet<String> blockedUsers;

  @ApiModelProperty(value = "FCM tokens", position = 6, example = "{\"29ba634d38ac6a1a\": \"dOMvrfckR9-5R_A43nuFMo:APA91bEVh2JQ8i-l1406C68mExotHQCGWeRc0cuLZTDH9t5vXXWIPZ-6HDaOtn1PLipsqWbpNWVcpDxkcIWwHNR60_mtaRo5kyuf0cs5Fxa6iGLpoqV93rpWIisa9_acGbOZwfIass0B\"}")
  private final Map<String, String> fcmTokens;

  @ApiModelProperty(value = "User createdAt", position = 7, dataType = "String", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
