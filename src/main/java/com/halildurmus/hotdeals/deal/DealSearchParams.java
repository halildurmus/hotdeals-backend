package com.halildurmus.hotdeals.deal;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DealSearchParams {

  private String query;

  private List<String> categories;

  private List<PriceRange> prices;

  private List<String> stores;

  private Boolean hideExpired;

  private String sortBy;

  private String order;
}
