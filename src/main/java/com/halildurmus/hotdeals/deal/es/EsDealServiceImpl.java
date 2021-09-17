package com.halildurmus.hotdeals.deal.es;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EsDealServiceImpl implements EsDealService {

  @Autowired
  private EsDealRepository repository;

  @Override
  public EsDeal saveOrUpdate(EsDeal esDeal) {
    return repository.save(esDeal);
  }

  @Override
  public Page<EsDeal> findAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public List<SearchHit<EsDeal>> queryDeals(String keyword, Pageable pageable) {
    return repository.queryDeals(keyword, pageable);
  }
}
