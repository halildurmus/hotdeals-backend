package com.halildurmus.hotdeals.deal.es;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DealSearchParams {

  private String query;
  private String category;
  private Double discountPriceFrom;
  private Double discountPriceTo;
  private String store;
  private String sortBy;
  private String order;

}
