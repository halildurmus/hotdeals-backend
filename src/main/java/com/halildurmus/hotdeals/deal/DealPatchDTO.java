package com.halildurmus.hotdeals.deal;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DealPatchDTO {

  @NotNull
  private DealStatus status;

}
