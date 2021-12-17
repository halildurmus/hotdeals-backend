package com.halildurmus.hotdeals.deal;

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
  private List<PriceRange> prices;
  private List<String> stores;
  private Boolean hideExpired;
  private String sortBy;
  private String order;

}
