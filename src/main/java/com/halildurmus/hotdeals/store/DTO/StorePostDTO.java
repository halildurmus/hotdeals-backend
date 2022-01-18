package com.halildurmus.hotdeals.store.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Schema(name = "StorePostDTO")
@Data
@Builder
public class StorePostDTO {

  @Schema(description = "Store name", example = "Amazon")
  @NotBlank
  private final String name;

  @Schema(description = "Store logo URL", example = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/2560px-Amazon_logo.svg.png")
  @URL
  @NotBlank
  private final String logo;

}