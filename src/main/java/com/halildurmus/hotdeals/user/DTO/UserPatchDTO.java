package com.halildurmus.hotdeals.user.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@ApiModel("UserPatchDTO")
@Data
@NoArgsConstructor
public class UserPatchDTO {

  @ApiModelProperty(value = "User avatar URL", position = 1, example = "https://www.gravatar.com/avatar")
  @URL
  @NotBlank
  private Optional<String> avatar;

  @ApiModelProperty(value = "User nickname", position = 2, example = "MrNobody123")
  @NotBlank
  @Size(min = 5, max = 25)
  private Optional<String> nickname;

}
