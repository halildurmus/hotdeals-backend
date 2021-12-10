package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealSearchParams {

  private String query;
  private List<String> categories;
  private List<Double> discountPricesFrom;
  private List<Double> discountPricesTo;
  private List<String> stores;
  private String sortBy;
  private String order;

}
