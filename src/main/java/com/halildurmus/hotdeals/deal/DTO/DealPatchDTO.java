package com.halildurmus.hotdeals.deal.DTO;

import com.halildurmus.hotdeals.deal.DealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "DealPatchDTO")
@Data
public class DealPatchDTO {

  @Schema(description = "Deal status", example = "EXPIRED")
  @NotNull
  private DealStatus status;

}
