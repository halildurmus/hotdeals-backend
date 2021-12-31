package com.halildurmus.hotdeals.user.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UserPostDTO {

  @NotBlank
  private final String uid;

  @URL
  @NotBlank
  private final String avatar;

  @Email
  @NotBlank
  private final String email;

}
