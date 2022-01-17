package com.halildurmus.hotdeals.user.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@ApiModel("UserBasicDTO")
@Data
@Builder
public class UserBasicDTO {

  @ApiModelProperty(value = "User ID", position = 1, example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @ApiModelProperty(value = "Firebase User ID", position = 2, example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  private final String uid;

  @ApiModelProperty(value = "User avatar URL", position = 3, example = "https://www.gravatar.com/avatar")
  private final String avatar;

  @ApiModelProperty(value = "User nickname", position = 4, example = "MrNobody123")
  private final String nickname;

  @ApiModelProperty(value = "User createdAt", position = 5, dataType = "String", example = "2021-06-30T16:36:59.713Z")
  private final Instant createdAt;

}
