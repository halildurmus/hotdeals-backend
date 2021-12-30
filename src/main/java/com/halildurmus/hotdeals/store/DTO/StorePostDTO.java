package com.halildurmus.hotdeals.store.DTO;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class StorePostDTO {

  @NotBlank
  private final String name;

  @URL
  @NotBlank
  private final String logo;

}