package com.halildurmus.hotdeals.deal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceRange {

  private Double from;

  private Double to;
}
