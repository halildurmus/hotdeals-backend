package com.halildurmus.hotdeals.user;

import java.util.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class UserPatchDTO {

  @URL
  @NotBlank
  private Optional<String> avatar;

  @NotBlank
  @Size(min = 5, max = 25)
  private Optional<String> nickname;

}
