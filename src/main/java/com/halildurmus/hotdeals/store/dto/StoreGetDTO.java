package com.halildurmus.hotdeals.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreGetDTO {

  @Schema(description = "Store id", example = "5fbe790ec6f0b32014074bb1")
  private final String id;

  @Schema(description = "Store name", example = "Amazon")
  private final String name;

  @Schema(
      description = "Store logo URL",
      example =
          "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/2560px-Amazon_logo.svg.png")
  private final String logo;
}
