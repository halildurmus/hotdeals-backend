package com.halildurmus.hotdeals.deal.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.halildurmus.hotdeals.deal.DealSearchParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EsDealService {

  Page<EsDeal> findAll(Pageable pageable);

  JsonNode getSuggestions(String query);

  JsonNode searchDeals(DealSearchParams searchParams, Pageable pageable);

  EsDeal save(EsDeal esDeal);
}
