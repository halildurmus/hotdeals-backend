package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface EsDealService {

  EsDeal save(EsDeal esDeal);

  Page<EsDeal> findAll(Pageable pageable);

  List<SearchHit<EsDeal>> queryDeals(String keyword, Pageable pageable);

}