package com.halildurmus.hotdeals.user.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@ApiModel("UserPostDTO")
@Data
@Builder
public class UserPostDTO {

  @ApiModelProperty(value = "Firebase User ID", position = 1, example = "ndj2KkbGwIUbfIUH2BT6700AQ832")
  @NotBlank
  private final String uid;

  @ApiModelProperty(value = "User avatar URL", position = 2, example = "https://www.gravatar.com/avatar")
  @URL
  @NotBlank
  private final String avatar;

  @ApiModelProperty(value = "User email address", position = 3, example = "halildurmus97@gmail.com")
  @Email
  @NotBlank
  private final String email;

}
