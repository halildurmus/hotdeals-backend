package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface EsDealService {

  EsDeal saveOrUpdate(EsDeal esDeal);

  void deleteByDealId(String id);

  Page<EsDeal> findAll(Pageable pageable);

  List<SearchHit<EsDeal>> queryDeals(String keyword, Pageable pageable);

}