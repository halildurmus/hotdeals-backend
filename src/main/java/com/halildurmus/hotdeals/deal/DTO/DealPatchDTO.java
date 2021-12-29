package com.halildurmus.hotdeals.deal.DTO;

import com.halildurmus.hotdeals.deal.DealStatus;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DealPatchDTO {

  @NotNull
  private DealStatus status;

}
